package nikodev.dex;

import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.function.Function;

public final class InstallSheetPermissionHook {
    private static final String INSTALL_SHEET_CLASS =
            "com.exteragram.messenger.plugins.ui.components.InstallPluginBottomSheet";
    private static final String UNIVERSAL_ADAPTER_CLASS =
            "org.telegram.ui.Components.UniversalAdapter";

    private static volatile Function<Object, Object> permissionViewProvider;
    private static XC_MethodHook.Unhook fillItemsHook;

    private InstallSheetPermissionHook() {}

    public static void setPermissionViewProvider(Function<Object, Object> provider) {
        permissionViewProvider = provider;
    }

    public static synchronized void installHook(ClassLoader appClassLoader) throws Exception {
        if (fillItemsHook != null) {
            return;
        }

        Class<?> installSheetClass = appClassLoader.loadClass(INSTALL_SHEET_CLASS);
        Method fillItems = findFillItemsMethod(installSheetClass);
        fillItems.setAccessible(true);
        fillItemsHook = XposedBridge.hookMethod(
                fillItems,
                new XC_MethodHook() {
                    @Override
                    public void afterHookedMethod(MethodHookParam param) {
                        appendPermissionItem(param);
                    }
                });
    }

    public static synchronized void refreshSheetItems(Object sheet) throws Exception {
        Field adapterField = sheet.getClass().getDeclaredField("adapter");
        adapterField.setAccessible(true);

        Object adapter = adapterField.get(sheet);
        if (adapter == null) {
            return;
        }

        Method update = adapter.getClass().getMethod("update", boolean.class);
        update.invoke(adapter, false);
    }

    public static synchronized void removeHook() {
        permissionViewProvider = null;
        if (fillItemsHook == null) {
            return;
        }

        fillItemsHook.unhook();
        fillItemsHook = null;
    }

    private static Method findFillItemsMethod(Class<?> installSheetClass)
            throws NoSuchMethodException {
        for (Method method : installSheetClass.getDeclaredMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (method.getName().equals("fillItems")
                    && parameterTypes.length == 2
                    && parameterTypes[0] == ArrayList.class
                    && parameterTypes[1].getName().equals(UNIVERSAL_ADAPTER_CLASS)) {
                return method;
            }
        }
        throw new NoSuchMethodException("InstallPluginBottomSheet.fillItems");
    }

    private static void appendPermissionItem(XC_MethodHook.MethodHookParam param) {
        Function<Object, Object> provider = permissionViewProvider;
        if (provider == null || param.args == null || param.args.length == 0) {
            return;
        }

        try {
            View permissionView = (View) provider.apply(param.thisObject);
            if (permissionView == null) {
                return;
            }

            ClassLoader appClassLoader = param.thisObject.getClass().getClassLoader();
            Class<?> itemClass = appClassLoader.loadClass("org.telegram.ui.Components.UItem");
            Object permissionItem = createCustomItem(itemClass, permissionView);
            ArrayList<Object> items = asItemList(param.args[0]);
            insertAfterDescription(param.thisObject, itemClass, items, permissionItem);
        } catch (Throwable error) {
            XposedBridge.log(error);
        }
    }

    private static Object createCustomItem(Class<?> itemClass, View permissionView)
            throws Exception {
        Method asCustom = itemClass.getMethod("asCustom", View.class);
        return asCustom.invoke(null, permissionView);
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<Object> asItemList(Object items) {
        return (ArrayList<Object>) items;
    }

    private static void insertAfterDescription(
            Object installSheet,
            Class<?> itemClass,
            ArrayList<Object> items,
            Object permissionItem)
            throws Exception {
        Field descriptionField = installSheet.getClass().getDeclaredField("descriptionView");
        descriptionField.setAccessible(true);
        Object descriptionView = descriptionField.get(installSheet);
        int insertionIndex = items.size();

        if (descriptionView != null) {
            Field itemViewField = itemClass.getField("view");
            for (int index = 0; index < items.size(); index++) {
                if (itemViewField.get(items.get(index)) == descriptionView) {
                    insertionIndex = index + 1;
                    break;
                }
            }
        }
        items.add(insertionIndex, permissionItem);
    }
}

package com.dbms.csmq.view.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

import javax.el.MethodExpression;

import oracle.adf.controller.ControllerContext;

import oracle.adf.view.rich.component.rich.RichPopup;

import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;

import javax.servlet.http.HttpSession;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.model.binding.DCParameter;
import oracle.adf.view.rich.component.rich.fragment.RichRegion;

import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.adfinternal.controller.state.BackingBeanScopeProviderImpl;

import oracle.adfinternal.controller.state.PageFlowScope;
import oracle.adfinternal.controller.state.RootViewPortContextImpl;
import oracle.adfinternal.controller.state.ViewScope;

import oracle.binding.AttributeBinding;
import oracle.binding.BindingContainer;
import oracle.binding.ControlBinding;
import oracle.binding.OperationBinding;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Key;
import oracle.jbo.Row;


import org.apache.myfaces.trinidad.context.RequestContext;


/**
 * Description:  A series of convenience functions for dealing with ADF Bindings.
 * @version 1.0
 * @author Duncan Mills
 * @author Steve Muench
 * @modified
 */
public class ADFUtils {
    /**
     * Get application module for an application module data control by name.
     * @param name application module data control name
     * @return ApplicationModule
     */
    public static ApplicationModule getApplicationModuleForDataControl(String name) {
        return (ApplicationModule)getEL("#{data." + name + ".dataProvider}");
    }

    /**
     * A convenience method for getting the value of a bound attribute in the
     * current page context programatically.
     * @param attributeName of the bound value in the pageDef
     * @return value of the attribute
     */
    public static Object getBoundAttributeValue(String attributeName) {
        return findControlBinding(attributeName).getInputValue();
    }

    public static String getBoundAttributeLabel(String attributeName) {
        return findControlBinding(attributeName).getLabel();
    }

    /**
     * A convenience method for setting the value of a bound attribute in the
     * context of the current page.
     * @param attributeName of the bound value in the pageDef
     * @param value to set
     */
    public static void setBoundAttributeValue(String attributeName, Object value) {
        findControlBinding(attributeName).setInputValue(value);
    }

    /**
     * Returns the evaluated value of a pageDef parameter.
     * @param pageDefName reference to the page definition file of the page with the parameter
     * @param parameterName name of the pagedef parameter
     * @return evaluated value of the parameter as a String
     */
    public static Object getPageDefParameterValue(String pageDefName, String parameterName) {
        BindingContainer bindings = findBindingContainer(pageDefName);
        DCParameter param = ((DCBindingContainer)bindings).findParameter(parameterName);
        return param.getValue();
    }

    /**
     * Convenience method to find a DCControlBinding as an AttributeBinding
     * to get able to then call getInputValue() or setInputValue() on it.
     * @param bindingContainer binding container
     * @param attributeName name of the attribute binding.
     * @return the control value binding with the name passed in.
     *
     */
    public static AttributeBinding findControlBinding(BindingContainer bindingContainer, String attributeName) {
        if (attributeName != null) {
            if (bindingContainer != null) {
                ControlBinding ctrlBinding = bindingContainer.getControlBinding(attributeName);
                if (ctrlBinding instanceof AttributeBinding) {
                    return (AttributeBinding)ctrlBinding;
                }
            }
        }
        return null;
    }

    /**
     * Convenience method to find a DCControlBinding as a JUCtrlValueBinding
     * to get able to then call getInputValue() or setInputValue() on it.
     * @param attributeName name of the attribute binding.
     * @return the control value binding with the name passed in.
     *
     */
    public static AttributeBinding findControlBinding(String attributeName) {
        return findControlBinding(getBindingContainer(), attributeName);
    }

    /**
     * Return the current page's binding container.
     * @return the current page's binding container
     */
    public static BindingContainer getBindingContainer() {
        return (BindingContainer)getEL("#{bindings}");
    }

    public static Object getEL(String expr) {
        FacesContext fc = FacesContext.getCurrentInstance();
        return fc.getApplication().evaluateExpressionGet(fc, expr, Object.class);
    }

    /**
     * Return the Binding Container as a DCBindingContainer.
     * @return current binding container as a DCBindingContainer
     */
    public static DCBindingContainer getDCBindingContainer() {
        return (DCBindingContainer)getBindingContainer();
    }

    /**
     * Get List of ADF Faces SelectItem for an iterator binding.
     *
     * Uses the value of the 'valueAttrName' attribute as the key for
     * the SelectItem key.
     *
     * @param iteratorName ADF iterator binding name
     * @param valueAttrName name of the value attribute to use
     * @param displayAttrName name of the attribute from iterator rows to display
     * @return ADF Faces SelectItem for an iterator binding
     */
    public static List<SelectItem> selectItemsForIterator(String iteratorName, String valueAttrName,
                                                          String displayAttrName) {
        return selectItemsForIterator(findIterator(iteratorName), valueAttrName, displayAttrName);
    }

    /**
     * Get List of ADF Faces SelectItem for an iterator binding with description.
     *
     * Uses the value of the 'valueAttrName' attribute as the key for
     * the SelectItem key.
     *
     * @param iteratorName ADF iterator binding name
     * @param valueAttrName name of the value attribute to use
     * @param displayAttrName name of the attribute from iterator rows to display
     * @param descriptionAttrName name of the attribute to use for description
     * @return ADF Faces SelectItem for an iterator binding with description
     */
    public static List<SelectItem> selectItemsForIterator(String iteratorName, String valueAttrName,
                                                          String displayAttrName, String descriptionAttrName) {
        return selectItemsForIterator(findIterator(iteratorName), valueAttrName, displayAttrName, descriptionAttrName);
    }

    /**
     * Get List of attribute values for an iterator.
     * @param iteratorName ADF iterator binding name
     * @param valueAttrName value attribute to use
     * @return List of attribute values for an iterator
     */
    public static List attributeListForIterator(String iteratorName, String valueAttrName) {
        return attributeListForIterator(findIterator(iteratorName), valueAttrName);
    }

    /**
     * Get List of Key objects for rows in an iterator.
     * @param iteratorName iterabot binding name
     * @return List of Key objects for rows
     */
    public static List<Key> keyListForIterator(String iteratorName) {
        return keyListForIterator(findIterator(iteratorName));
    }

    /**
     * Get List of Key objects for rows in an iterator.
     * @param iter iterator binding
     * @return List of Key objects for rows
     */
    public static List<Key> keyListForIterator(DCIteratorBinding iter) {
        List attributeList = new ArrayList();
        for (Row r : iter.getAllRowsInRange()) {
            attributeList.add(r.getKey());
        }
        return attributeList;
    }

    /**
     * Get List of Key objects for rows in an iterator using key attribute.
     * @param iteratorName iterator binding name
     * @param keyAttrName name of key attribute to use
     * @return List of Key objects for rows
     */
    public static List<Key> keyAttrListForIterator(String iteratorName, String keyAttrName) {
        return keyAttrListForIterator(findIterator(iteratorName), keyAttrName);
    }

    /**
     * Get List of Key objects for rows in an iterator using key attribute.
     *
     * @param iter iterator binding
     * @param keyAttrName name of key attribute to use
     * @return List of Key objects for rows
     */
    public static List<Key> keyAttrListForIterator(DCIteratorBinding iter, String keyAttrName) {
        List attributeList = new ArrayList();
        for (Row r : iter.getAllRowsInRange()) {
            attributeList.add(new Key(new Object[] { r.getAttribute(keyAttrName) }));
        }
        return attributeList;
    }

    /**
     * Get a List of attribute values for an iterator.
     *
     * @param iter iterator binding
     * @param valueAttrName name of value attribute to use
     * @return List of attribute values
     */
    public static List attributeListForIterator(DCIteratorBinding iter, String valueAttrName) {
        List attributeList = new ArrayList();
        for (Row r : iter.getAllRowsInRange()) {
            attributeList.add(r.getAttribute(valueAttrName));
        }
        return attributeList;
    }

    /**
     * Find an iterator binding in the current binding container by name.
     *
     * @param name iterator binding name
     * @return iterator binding
     */
    public static DCIteratorBinding findIterator(String name) {
        DCIteratorBinding iter = getDCBindingContainer().findIteratorBinding(name);
        if (iter == null) {
            throw new RuntimeException("Iterator '" + name + "' not found");
        }
        return iter;
    }

    /**
     * Get List of ADF Faces SelectItem for an iterator binding with description.
     *
     * Uses the value of the 'valueAttrName' attribute as the key for
     * the SelectItem key.
     *
     * @param iter ADF iterator binding
     * @param valueAttrName name of value attribute to use for key
     * @param displayAttrName name of the attribute from iterator rows to display
     * @param descriptionAttrName name of the attribute for description
     * @return ADF Faces SelectItem for an iterator binding with description
     */
    public static List<SelectItem> selectItemsForIterator(DCIteratorBinding iter, String valueAttrName,
                                                          String displayAttrName, String descriptionAttrName) {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        for (Row r : iter.getAllRowsInRange()) {
            selectItems.add(new SelectItem(r.getAttribute(valueAttrName), (String)r.getAttribute(displayAttrName),
                                           (String)r.getAttribute(descriptionAttrName)));
        }
        return selectItems;
    }

    /**
     * Get List of ADF Faces SelectItem for an iterator binding.
     *
     * Uses the value of the 'valueAttrName' attribute as the key for
     * the SelectItem key.
     *
     * @param iter ADF iterator binding
     * @param valueAttrName name of value attribute to use for key
     * @param displayAttrName name of the attribute from iterator rows to display
     * @return ADF Faces SelectItem for an iterator binding
     */
    public static List<SelectItem> selectItemsForIterator(DCIteratorBinding iter, String valueAttrName,
                                                          String displayAttrName) {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        for (Row r : iter.getAllRowsInRange()) {
            selectItems.add(new SelectItem(r.getAttribute(valueAttrName), (String)r.getAttribute(displayAttrName)));
        }
        return selectItems;
    }

    /**
     * Get List of ADF Faces SelectItem for an iterator binding.
     *
     * Uses the rowKey of each row as the SelectItem key.
     *
     * @param iteratorName ADF iterator binding name
     * @param displayAttrName name of the attribute from iterator rows to display
     * @return ADF Faces SelectItem for an iterator binding
     */
    public static List<SelectItem> selectItemsByKeyForIterator(String iteratorName, String displayAttrName) {
        return selectItemsByKeyForIterator(findIterator(iteratorName), displayAttrName);
    }

    /**
     * Get List of ADF Faces SelectItem for an iterator binding with discription.
     *
     * Uses the rowKey of each row as the SelectItem key.
     *
     * @param iteratorName ADF iterator binding name
     * @param displayAttrName name of the attribute from iterator rows to display
     * @param descriptionAttrName name of the attribute for description
     * @return ADF Faces SelectItem for an iterator binding with discription
     */
    public static List<SelectItem> selectItemsByKeyForIterator(String iteratorName, String displayAttrName,
                                                               String descriptionAttrName) {
        return selectItemsByKeyForIterator(findIterator(iteratorName), displayAttrName, descriptionAttrName);
    }

    /**
     * Get List of ADF Faces SelectItem for an iterator binding with discription.
     *
     * Uses the rowKey of each row as the SelectItem key.
     *
     * @param iter ADF iterator binding
     * @param displayAttrName name of the attribute from iterator rows to display
     * @param descriptionAttrName name of the attribute for description
     * @return ADF Faces SelectItem for an iterator binding with discription
     */
    public static List<SelectItem> selectItemsByKeyForIterator(DCIteratorBinding iter, String displayAttrName,
                                                               String descriptionAttrName) {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        for (Row r : iter.getAllRowsInRange()) {
            selectItems.add(new SelectItem(r.getKey(), (String)r.getAttribute(displayAttrName),
                                           (String)r.getAttribute(descriptionAttrName)));
        }
        return selectItems;
    }

    /**
     * Get List of ADF Faces SelectItem for an iterator binding.
     *
     * Uses the rowKey of each row as the SelectItem key.
     *
     * @param iter ADF iterator binding
     * @param displayAttrName name of the attribute from iterator rows to display
     * @return List of ADF Faces SelectItem for an iterator binding
     */
    public static List<SelectItem> selectItemsByKeyForIterator(DCIteratorBinding iter, String displayAttrName) {
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        for (Row r : iter.getAllRowsInRange()) {
            selectItems.add(new SelectItem(r.getKey(), (String)r.getAttribute(displayAttrName)));
        }
        return selectItems;
    }


    /**
     * Find the BindingContainer for a page definition by name.
     *
     * Typically used to refer eagerly to page definition parameters. It is
     * not best practice to reference or set bindings in binding containers
     * that are not the one for the current page.
     *
     * @param pageDefName name of the page defintion XML file to use
     * @return BindingContainer ref for the named definition
     */
    private static BindingContainer findBindingContainer(String pageDefName) {
        //BindingContext bctx = getDCBindingContainer().getBindingContext();
        BindingContext bctx =
            BindingContext.getCurrent(); // TODO commented above line bcoz getDCBindingContainer() throwing FacesContext not found exception
        BindingContainer foundContainer = bctx.findBindingContainer(pageDefName);
        return foundContainer;
    }

    public static OperationBinding findOperationBinding(String opName) {
        BindingContext bcx = BindingContext.getCurrent();
        DCBindingContainer bc = (DCBindingContainer)bcx.getCurrentBindingsEntry();
        OperationBinding ob = bc.getOperationBinding(opName);
        return ob;
    }

    public static OperationBinding findOperationBinding(String pageDefName, String opName) {
        DCBindingContainer bc = (DCBindingContainer)findBindingContainer(pageDefName);
        OperationBinding ob = bc.getOperationBinding(opName);
        return ob;
    }

    /**
     * This is a helper method to call operation binding with all needed params
     */
    public static Object invokeOperationBinding(String pageDefName, String opName, Map<String, Object> paramMap) {

        OperationBinding ob = findOperationBinding(pageDefName, opName);

        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            ob.getParamsMap().put(entry.getKey(), entry.getValue());
        }

        Object result = ob.execute();
        return result;
    }

    public static Object invokeOperationBinding(String opName, Map<String, Object> paramMap) {
        OperationBinding ob = findOperationBinding(opName);
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            ob.getParamsMap().put(entry.getKey(), entry.getValue());
        }
        Object result = ob.execute();
        return result;
    }

    /**
     * This is a helper method to navigate to specified control flow case.
     * @param controlFlowCase
     */
    public static void navigateToControlFlowCase(String controlFlowCase) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        Application application = fctx.getApplication();
        NavigationHandler navHandler = application.getNavigationHandler();
        navHandler.handleNavigation(fctx, null, controlFlowCase);
    }

    /**
     * This is a helper method to apply partial target programatically.
     * @param component
     */
    public static void addPartialTarget(UIComponent component) {
        RequestContext adfContext;
        adfContext = RequestContext.getCurrentInstance();
        adfContext.addPartialTarget(component);
    }

    /**
     * Method to refresh Iterator
     */
    public static void refreshIterator(String iteratorName) {
        DCIteratorBinding iter = findIterator(iteratorName);

        if (iter != null) {
            iter.refreshIfNeeded();
            iter.executeQuery();
        }
    }

    public static void showPopup(RichPopup popup) {
        RichPopup.PopupHints popupHints = new RichPopup.PopupHints();
        popup.show(popupHints);
    }
    /*NOT TO USE SHOWPOPUP OR HIDEPOPUP USING POPUPID */
    //    public static void showPopup(String popupId,String alignId){
    //        FacesContext context = FacesContext.getCurrentInstance();
    //        StringBuilder script = new StringBuilder();
    //        script.append("var popup=AdfPage.PAGE.findComponent('").append(popupId).append("');").append("if (!popup.isPopupVisible()){").append("var hints = {}; ").append("hints[AdfRichPopup.HINT_ALIGN_ID]='").append(alignId).append("';").append("hints[AdfRichPopup.HINT_ALIGN]=AdfRichPopup.ALIGN_END_AFTER; ").append("popup.show(hints);}");
    //        ExtendedRenderKitService erks = Service.getService(context.getRenderKit(), ExtendedRenderKitService.class);
    //        erks.addScript(context, script.toString());
    //    }
    //
    //    public  static void showPopup(String popupId) {
    //        FacesContext context = null;
    //        ExtendedRenderKitService extRenderKitSrvc = null;
    //        StringBuilder script = null;
    //        context = FacesContext.getCurrentInstance();
    //        extRenderKitSrvc = Service.getRenderKitService(context, ExtendedRenderKitService.class);
    //        script = new StringBuilder();
    //        script.append("var popup = AdfPage.PAGE.findComponent('").append(popupId).append("'); ").append("popup.show();");
    //        extRenderKitSrvc.addScript(context, script.toString());
    //    }
    //    public static void hidePopup(String popupId) {
    //        FacesContext context = FacesContext.getCurrentInstance();
    //        ExtendedRenderKitService erkService = Service.getService(context.getRenderKit(), ExtendedRenderKitService.class);
    //        erkService.addScript(context,
    //                //                         "AdfPage.PAGE.findComponent('" + popupId + "').hide();");
    //                "AdfPage.PAGE.findComponent('" + popupId + "').hide();");
    //
    //    }

    public static void queueActionEventInRegion(RichRegion region, String outcome) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ExpressionFactory expressionFactory = null;
        expressionFactory = fctx.getApplication().getExpressionFactory();
        ELContext elctx = fctx.getELContext();
        MethodExpression methodExpression = null;
        methodExpression = expressionFactory.createMethodExpression(elctx, outcome, String.class, new Class[] { });
        region.queueActionEventInRegion(methodExpression, null, null, false, 0, 0, PhaseId.INVOKE_APPLICATION);
    }

    public static Map<String, Object> getBackingBeanScopeMap() {
        Map<String, Object> backingBeanScopeMap = null;
        AdfFacesContext adfFacesContext = null;
        adfFacesContext = AdfFacesContext.getCurrentInstance();
        BackingBeanScopeProviderImpl provider =
            (BackingBeanScopeProviderImpl)adfFacesContext.getBackingBeanScopeProvider();
        backingBeanScopeMap = provider.getCurrentScope();
        return backingBeanScopeMap;
    }

    public static Map<String, Object> getSessionScope() {
        Map<String, Object> sessionScope = null;
        FacesContext fctx = FacesContext.getCurrentInstance();
        ExternalContext ectx = fctx.getExternalContext();
        sessionScope = ectx.getSessionMap();
        return sessionScope;
    }

    public static HttpSession getSession() {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ExternalContext ectx = fctx.getExternalContext();
        HttpSession session = (HttpSession)ectx.getSession(false);
        return session;
    }

    /**
     * This method is responsible to get the Globl View Scope.
     * @return
     */

    //    public static ViewScope getGlobalViewScope() {
    //        AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
    //        ControllerContext conn = ControllerContext.getInstance();
    //        RootViewPortContextImpl rootViewPort = (RootViewPortContextImpl)conn.getCurrentRootViewPort();
    //        ViewScope vss = rootViewPort.getViewScope(adfFacesContext);
    //        return vss;
    //    }
    //
    //    /**
    //     * This method is responsible to get the Globl Page Flow Scope.
    //     * @return
    //     */
    //    public static PageFlowScope getGlobalPageFlowScope() {
    //        ControllerContext conn = ControllerContext.getInstance();
    //        RootViewPortContextImpl rootViewPort = (RootViewPortContextImpl)conn.getCurrentRootViewPort();
    //        PageFlowScope pfs = rootViewPort.getPageFlowScopeMap();
    //        return pfs;
    //    }
}

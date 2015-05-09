package com.dbms.csmq.view.backing.publish;


import com.dbms.csmq.CSMQBean;
import com.dbms.csmq.UserBean;
import com.dbms.csmq.view.backing.NMQ.NMQUtils;

import com.dbms.csmq.view.backing.NMQ.NMQWizardBean;
import com.dbms.util.Utils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.input.RichSelectManyShuttle;
import oracle.adf.view.rich.component.rich.nav.RichCommandButton;
import oracle.adf.view.rich.component.rich.nav.RichCommandToolbarButton;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;


public class WorkflowBean {


    List list = new ArrayList<javax.faces.model.SelectItem>();
    List selectedTerms;
    private RichSelectManyShuttle sms1;
    private UISelectItems si1;
    private RichCommandButton cb1;
    private UserBean userBean;
    private CSMQBean csmqBean;
    private NMQWizardBean nMQWizardBean;
    private String currentRequestor;
    private String dictContentIDWithError;
    private RichTable cntrlRelationErrors;
    private RichTable cntrlContentErrors;
    private int errorCount = 1;
    private RichCommandToolbarButton cntrlActivateButton;
    private RichInputText controlMQState;

    public String init() {
        //userBean.setCurrentMenuPath("Publish");
        //userBean.setCurrentMenu("NON_IMPACT_PUBLISH");
        
        return null;
    }

    public String IA_init() {
        //userBean.setCurrentMenuPath("Impact Analysis Publish");
        //userBean.setCurrentMenu("IMPACT_PUBLISH");
        return null;
    }

    public WorkflowBean() {
        userBean = (UserBean)ADFContext.getCurrent().getSessionScope().get("UserBean");
        csmqBean = (CSMQBean)ADFContext.getCurrent().getApplicationScope().get("CSMQBean");
        nMQWizardBean = (NMQWizardBean)ADFContext.getCurrent().getPageFlowScope().get("NMQWizardBean");
        currentRequestor = userBean.getCurrentUser();
        userBean.setCurrentMenuPath("Confirm");
        userBean.setCurrentMenu("CONFIRM");
    
        }

    private void loadPromoteToPublished () { 

        //BindingContext bc = BindingContext.getCurrent();
        //DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        
        DCBindingContainer binding = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("ViewObjTermsByState1Iterator");
        ViewObject vo = dciterb.getViewObject();
        vo.setNamedWhereClauseParam("state", CSMQBean.STATE_APPROVED);
        vo.setNamedWhereClauseParam("activationGroup", csmqBean.getDefaultPublishReleaseGroup());      
 
        CSMQBean.logger.info(userBean.getCaller() + " ** REQUERY **");
        CSMQBean.logger.info(userBean.getCaller() + " Iterator: ViewObjTermsByState1Iterator");
        CSMQBean.logger.info(userBean.getCaller() + " state: " + CSMQBean.STATE_APPROVED);
        CSMQBean.logger.info(userBean.getCaller() + " activationGroup: " + csmqBean.getDefaultPublishReleaseGroup());
        vo.executeQuery();
        
        AdfFacesContext.getCurrentInstance().addPartialTarget(sms1);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(sms1);
        
        }



    public List getSelectedTerms() {
        if (selectedTerms == null) {
            selectedTerms = new ArrayList<javax.faces.model.SelectItem>();
        }
        return selectedTerms;
    }

    public void setSelectedTerms(List selectedItems) {
        this.selectedTerms = selectedItems;
    }

    public void activate(DialogEvent actionEvent) {
        NMQUtils.activateGroup(CSMQBean.getProperty("DEFAULT_PUBLISH_RELEASE_GROUP"));
    }

    public void demoteToDraft(DialogEvent actionEvent) {
        changeState(CSMQBean.STATE_DRAFT, CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"));
    }
    
    public void demoteToPendingImpactAssessment (DialogEvent actionEvent) {
        changeState(CSMQBean.STATE_PENDING_IMPACT_ASSESSMENT, CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"));
    }
    
    public void activateInCheckMode(ActionEvent actionEvent) {  
        if (NMQUtils.activateGroupInCheckMode(CSMQBean.getProperty("DEFAULT_PUBLISH_RELEASE_GROUP"))) {
            this.cntrlActivateButton.setDisabled(false);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlActivateButton);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlActivateButton);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlRelationErrors);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlRelationErrors);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlContentErrors);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlContentErrors);
            }
        }

    public void promoteToPublished(DialogEvent actionEvent) {
        changeState(CSMQBean.STATE_PUBLISHED, CSMQBean.getProperty("DEFAULT_PUBLISH_RELEASE_GROUP"));
        AdfFacesContext.getCurrentInstance().addPartialTarget(sms1);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(sms1);
        }


    public void promoteSingleMQToPublished(DialogEvent actionEvent)  {
        Hashtable h = new Hashtable();
        h = NMQUtils.changeStateFromDraftToPublish(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_REQUESTED, userBean.getCurrentUser(), userBean.getUserRole(), null, "Publish MedDRA Query", CSMQBean.getProperty("DEFAULT_PUBLISH_RELEASE_GROUP"));           
        System.out.println("Changing state : " + h);
        if (h.get("STATE").equals("Published")) {
            nMQWizardBean.setCurrentState(CSMQBean.STATE_PUBLISHED);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cb1);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cb1);
            AdfFacesContext.getCurrentInstance().addPartialTarget(controlMQState);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(controlMQState);
            
            }
            
         /*   
        if (nMQWizardBean.getCurrentState().equals(CSMQBean.STATE_PROPOSED )){
                System.out.println("*** CHANGING STATE FROM STATE_PROPOSED TO:");
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_REQUESTED, userBean.getCurrentUser(), userBean.getUserRole(), null, "Publish MedDRA Query", CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"), false);
                System.out.println("...STATE_PROPOSED: " + h);
                try {Thread.sleep(5000);} catch (InterruptedException e) {}
                
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_DRAFT, userBean.getCurrentUser(), userBean.getUserRole(), null, null, CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"), false);
                System.out.println("...STATE_DRAFT: " + h);
                try {Thread.sleep(5000);} catch (InterruptedException e) {}
                
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_REVIEWED, userBean.getCurrentUser(), userBean.getUserRole(), nMQWizardBean.getCurrentRequestedByDate(), null, CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"), false);
                System.out.println("...STATE_REVIEWED: " + h);
                try {Thread.sleep(5000);} catch (InterruptedException e) {}
                
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_APPROVED, userBean.getCurrentUser(), userBean.getUserRole(), nMQWizardBean.getCurrentRequestedByDate(), null, CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"), false);
                System.out.println("...STATE_APPROVED: " + h);
                try {Thread.sleep(5000);} catch (InterruptedException e) {}
                
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_PUBLISHED, userBean.getCurrentUser(), userBean.getUserRole(), null, null, CSMQBean.getProperty("DEFAULT_PUBLISH_RELEASE_GROUP"), true);
                System.out.println("...STATE_PUBLISHED: " + h);
                }
        
            else if (nMQWizardBean.getCurrentState().equals(CSMQBean.STATE_REQUESTED)){
                System.out.println("*** CHANGING STATE FROM STATE_REQUESTED TO:");
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_DRAFT, userBean.getCurrentUser(), userBean.getUserRole(), null, null, CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"), false);
                System.out.println("...STATE_DRAFT: " + h);
                try {Thread.sleep(5000);} catch (InterruptedException e) {}
                
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_REVIEWED, userBean.getCurrentUser(), userBean.getUserRole(), nMQWizardBean.getCurrentRequestedByDate(), null, CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"), false);
                System.out.println("...STATE_REVIEWED: " + h);
                try {Thread.sleep(5000);} catch (InterruptedException e) {}
                
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_APPROVED, userBean.getCurrentUser(), userBean.getUserRole(), nMQWizardBean.getCurrentRequestedByDate(), null, CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"), false);
                System.out.println("...STATE_APPROVED: " + h);
                try {Thread.sleep(5000);} catch (InterruptedException e) {}
                
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_PUBLISHED, userBean.getCurrentUser(), userBean.getUserRole(), null, null, CSMQBean.getProperty("DEFAULT_PUBLISH_RELEASE_GROUP"), true);
                System.out.println("...STATE_PUBLISHED: " + h);
                }
            
            else if (nMQWizardBean.getCurrentState().equals(CSMQBean.STATE_DRAFT)){
                System.out.println("*** CHANGING STATE FROM STATE_DRAFT TO:");
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_REVIEWED, userBean.getCurrentUser(), userBean.getUserRole(), nMQWizardBean.getCurrentRequestedByDate(), null, CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"), false);
                    System.out.println("...STATE_REVIEWED: " + h);
                try {Thread.sleep(5000);} catch (InterruptedException e) {}
                
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_APPROVED, userBean.getCurrentUser(), userBean.getUserRole(), nMQWizardBean.getCurrentRequestedByDate(), null, CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"), false);
                    System.out.println("...STATE_APPROVED: " + h);
                try {Thread.sleep(5000);} catch (InterruptedException e) {}
                
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_PUBLISHED, userBean.getCurrentUser(), userBean.getUserRole(), null, null, CSMQBean.getProperty("DEFAULT_PUBLISH_RELEASE_GROUP"), true);
                    System.out.println("...STATE_PUBLISHED: " + h);
                }    
           
            else if (nMQWizardBean.getCurrentState().equals(CSMQBean.STATE_REVIEWED)){
                System.out.println("*** CHANGING STATE FROM STATE_REVIEWED TO:");
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_APPROVED, userBean.getCurrentUser(), userBean.getUserRole(), nMQWizardBean.getCurrentRequestedByDate(), null, CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"), false);
                    System.out.println("...STATE_APPROVED: " + h);
                try {Thread.sleep(5000);} catch (InterruptedException e) {}
                
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_PUBLISHED, userBean.getCurrentUser(), userBean.getUserRole(), null, null, CSMQBean.getProperty("DEFAULT_PUBLISH_RELEASE_GROUP"), true);
                    System.out.println("...STATE_PUBLISHED: " + h);
                }     
            
            else if (nMQWizardBean.getCurrentState().equals(CSMQBean.STATE_APPROVED)){
                System.out.println("*** CHANGING STATE FROM STATE_APPROVED TO:");
                h = NMQUtils.changeState(nMQWizardBean.getCurrentDictContentID(), CSMQBean.STATE_PUBLISHED, userBean.getCurrentUser(), userBean.getUserRole(), null, null, CSMQBean.getProperty("DEFAULT_PUBLISH_RELEASE_GROUP"), true);
                    System.out.println("...STATE_PUBLISHED: " + h);
                }    
            */
            
            }

    public void promoteToPublishedIA(DialogEvent actionEvent) {
        changeState(CSMQBean.IA_STATE_PUBLISHED, CSMQBean.getProperty("DEFAULT_MEDDRA_RELEASE_GROUP"));
        AdfFacesContext.getCurrentInstance().addPartialTarget(sms1);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(sms1);
        }


    private void changeState(String state, String activationGroup) {
        String terms = "";
        for (int i = 0; i < list.size(); i++)
            terms += list.get(i) + ",";
        
        if (terms.length() < 1) {    
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select at least one term.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
            }
        terms = terms.substring(0, terms.length() - 1);
        NMQUtils.changeState(terms, state, userBean.getCurrentUser(), userBean.getUserRole(), null, null, activationGroup);
    }

    public void setSms1(RichSelectManyShuttle sms1) {
        this.sms1 = sms1;
    }

    public RichSelectManyShuttle getSms1() {
        return sms1;
    }

    public void setSi1(UISelectItems si1) {
        this.si1 = si1;
    }

    public UISelectItems getSi1() {
        return si1;
    }

    public void setCb1(RichCommandButton cb1) {
        this.cb1 = cb1;
    }

    public RichCommandButton getCb1() {
        return cb1;
    }

    public void termListChanged(ValueChangeEvent valueChangeEvent) {
        list = (ArrayList)valueChangeEvent.getNewValue();
    }

    public void setDictContentIDWithError(String dictContentIDWithError) {
        this.dictContentIDWithError = dictContentIDWithError;
    }

    public String getDictContentIDWithError() {
        return dictContentIDWithError;
    }

    public void setCntrlRelationErrors(RichTable cntrlRelationErrors) {
        this.cntrlRelationErrors = cntrlRelationErrors;
    }

    public RichTable getCntrlRelationErrors() {
        return cntrlRelationErrors;
    }

    public void contentsErrorRowChanged(SelectionEvent selectionEvent) {
        resolveMethodExpression("#{bindings.ViewObjActivationErrors_Contents1.collectionModel.makeCurrent}", null,
                                new Class[] { SelectionEvent.class }, new Object[] { selectionEvent });
        RichTable object = (RichTable)selectionEvent.getSource();
        Row row = null;
        for (Object facesRowKey : object.getSelectedRowKeys()) {
            object.setRowKey(facesRowKey);
            Object o = object.getRowData();
            JUCtrlHierNodeBinding rowData = (JUCtrlHierNodeBinding)o;
            row = rowData.getRow();
            }

        if (row == null)
            return;

        dictContentIDWithError = Utils.getAsString(row, "PredictContentId");
        AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlRelationErrors);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlRelationErrors);
        }


    public Object resolveMethodExpression(String expression, Class returnType, Class[] argTypes, Object[] argValues) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application app = facesContext.getApplication();
        ExpressionFactory elFactory = app.getExpressionFactory();
        ELContext elContext = facesContext.getELContext();
        MethodExpression methodExpression =
            elFactory.createMethodExpression(elContext, expression, returnType, argTypes);
        return methodExpression.invoke(elContext, argValues);
    }

    public void demoteSelected(DialogEvent dialogEvent) {
        String terms = "";
        
        RowKeySet rksSelectedRows = this.cntrlContentErrors.getSelectedRowKeys();
        Iterator itrSelectedRows = rksSelectedRows.iterator();
     
        // Get the data control that is bound to the table - e.g. OpenSupportItemsIterator
        DCBindingContainer bindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding dcIteratorBindings = bindings.findIteratorBinding("ViewObjActivationErrors_Contents1Iterator");
        RowSetIterator rsiSelectedRows = dcIteratorBindings.getRowSetIterator();

        while (itrSelectedRows.hasNext()) {
            Key key = (Key)((List)itrSelectedRows.next()).get(0);
            Row myRow = rsiSelectedRows.getRow(key);
            String pci = myRow.getAttribute("PredictContentId").toString();
            terms+=pci + ",";
            }

        terms = terms.substring(0, terms.length()-1);
        NMQUtils.changeState(terms, CSMQBean.STATE_DRAFT, userBean.getCurrentUser(), userBean.getUserRole(), null, null, CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP")); 
        }


    public void setCntrlContentErrors(RichTable cntrlContentErrors) {
        this.cntrlContentErrors = cntrlContentErrors;
        }

    public RichTable getCntrlContentErrors() {
        if (cntrlContentErrors != null) errorCount = cntrlContentErrors.getRowCount();
        return cntrlContentErrors;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getErrorCount() {
        return errorCount;
    }


    public void setCntrlActivateButton(RichCommandToolbarButton cntrlActivateButton) {
        this.cntrlActivateButton = cntrlActivateButton;
    }

    public RichCommandToolbarButton getCntrlActivateButton() {
        return cntrlActivateButton;
    }

    public String test() {
        loadPromoteToPublished ();
        return null;
    }

    public void setControlMQState(RichInputText controlMQState) {
        this.controlMQState = controlMQState;
    }

    public RichInputText getControlMQState() {
        return controlMQState;
    }
}

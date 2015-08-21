package com.dbms.csmq.view.backing.whod;


import com.dbms.csmq.CSMQBean;
import com.dbms.csmq.UserBean;
import com.dbms.csmq.view.hierarchy.WhodTermHierarchyBean;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.view.rich.component.rich.input.RichInputDate;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.input.RichSelectBooleanCheckbox;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.component.rich.layout.RichPanelGroupLayout;
import oracle.adf.view.rich.component.rich.layout.RichToolbar;
import oracle.adf.view.rich.component.rich.nav.RichCommandButton;
import oracle.adf.view.rich.component.rich.nav.RichTrain;
import oracle.adf.view.rich.component.rich.nav.RichTrainButtonBar;
import oracle.adf.view.rich.component.rich.output.RichOutputFormatted;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.AttributeContext;
import oracle.binding.ManagedDataControl;
import oracle.binding.OperationBinding;
import oracle.binding.RowContext;
import oracle.binding.TransactionalDataControl;
import oracle.binding.UpdateableDataControl;

import oracle.jbo.domain.Date;


public class WhodWizardUIBean implements TransactionalDataControl, UpdateableDataControl, ManagedDataControl {

    // DETAILS CONTROLS

    private RichInputText controlRequestorID;
    private RichInputText controlCreateDate;

    private RichSelectOneChoice controlMQLevel;

    private RichSelectOneChoice controlReleaseGroup;
    private RichInputText controlMQState;
    private RichInputText controlMQCode;
    private RichInputText controlDictionaryVersion;
    private RichInputText controlMqAlgorithm;
    private RichSelectOneChoice controlMQScope;


    // INF NOTES CONTROLS
    private RichInputText controlInfNoteMQDescription;
    private RichInputText controlInfNoteMQSource;
    private RichInputText controlInfNoteMQNotes;

    // Status controls
    private RichInputText ctrlActiveDictionary;
    private RichInputText controlMQStatus;

    private boolean standardProduct = true;

    private RichInputText controlMQName;
    private RichInputText controlNMQCode;
    private RichOutputFormatted controlWizardStatus;
    private CSMQBean cSMQBean;
    private UserBean userBean;
    private RichInputText controlCurrentTermName;
    private RichInputText controlRequestor;
    private RichTrain cntrlTrain;
    private RichTrainButtonBar cntrlTrainButtons;

    private RichInputText cntrlReasonForRequest;
    private oracle.jbo.domain.Date currentDateRequested;
    private oracle.jbo.domain.Date currentRequestedByDate;
    private RichInputDate cntrlDateRequested;
    private RichCommandButton cntrlApproved;
    private RichCommandButton cntrlReviewed;
    private RichPanelGroupLayout cntrlConfirmDetailsPanel;
    private RichToolbar cntrlConfirmToolbar;
    private RichSelectOneChoice cntrlScope;

    private RichSelectBooleanCheckbox cntrlIncludeLLTsInExport;
    private RichSelectBooleanCheckbox cntrlExportDisplayedOnly;

    private WhodWizardBean whodWizardBean;
    private WhodTermHierarchyBean termHierarchyBean;
    private UISelectItems cntrlProductSelectItems;


    public WhodWizardUIBean() {
        whodWizardBean = (WhodWizardBean)AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodWizardBean");
        termHierarchyBean =
                (WhodTermHierarchyBean)AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodTermHierarchyBean");
        userBean = whodWizardBean.getUserBean();
        cSMQBean = whodWizardBean.getCSMQBean();
    }


    public void setFooterInfo(String termName) {
        whodWizardBean.setCurrentTermName(termName);
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.controlCurrentTermName);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(this.controlCurrentTermName);
    }


    /*
     * @author MTW
     * 07/21/2014
     *
     */

    public void designeeValueChange(ValueChangeEvent valueChangeEvent) {

        whodWizardBean.setDesigneeList((List<String>)valueChangeEvent.getNewValue());

    }

    public void setControlMQStatus(RichInputText controlMQStatus) {
        this.controlMQStatus = controlMQStatus;
    }

    public RichInputText getControlMQStatus() {
        return controlMQStatus;
    }

    public void setCurrentDictContentID(String currentDictContentID) {
        whodWizardBean.setCurrentDictContentID(currentDictContentID);
    }

    public String getCurrentDictContentID() {
        return whodWizardBean.getCurrentDictContentID();
    }

    public void setControlRequestorID(RichInputText controlRequestorID) {
        this.controlRequestorID = controlRequestorID;
    }

    public RichInputText getControlRequestorID() {
        return controlRequestorID;
    }

    public void setControlCreateDate(RichInputText controlCreateDate) {
        this.controlCreateDate = controlCreateDate;
    }

    public RichInputText getControlCreateDate() {
        return controlCreateDate;
    }


    public void setControlMQLevel(RichSelectOneChoice controlMQLevel) {
        this.controlMQLevel = controlMQLevel;
    }

    public RichSelectOneChoice getControlMQLevel() {
        return controlMQLevel;
    }


    public void setControlReleaseGroup(RichSelectOneChoice controlReleaseGroup) {
        this.controlReleaseGroup = controlReleaseGroup;
    }

    public RichSelectOneChoice getControlReleaseGroup() {
        return controlReleaseGroup;
    }

    public void setControlMQState(RichInputText controlMQState) {
        this.controlMQState = controlMQState;
    }

    public RichInputText getControlMQState() {
        return controlMQState;
    }

    public void setControlMQCode(RichInputText controlMQCode) {
        this.controlMQCode = controlMQCode;
    }

    public RichInputText getControlMQCode() {
        return controlMQCode;
    }

    public void setControlDictionaryVersion(RichInputText controlDictionaryVersion) {
        this.controlDictionaryVersion = controlDictionaryVersion;
    }

    public RichInputText getControlDictionaryVersion() {
        return controlDictionaryVersion;
    }

    public void setControlMqAlgorithm(RichInputText controlMqAlgorithm) {
        this.controlMqAlgorithm = controlMqAlgorithm;
    }

    public RichInputText getControlMqAlgorithm() {
        return controlMqAlgorithm;
    }


    public void setControlInfNoteMQDescription(RichInputText controlInfNoteMQDescription) {
        this.controlInfNoteMQDescription = controlInfNoteMQDescription;
    }

    public RichInputText getControlInfNoteMQDescription() {
        return controlInfNoteMQDescription;
    }


    public void setControlInfNoteMQNotes(RichInputText controlInfNoteMQNotes) {
        this.controlInfNoteMQNotes = controlInfNoteMQNotes;
    }

    public RichInputText getControlInfNoteMQNotes() {
        return controlInfNoteMQNotes;
    }

    public void setControlMQScope(RichSelectOneChoice controlMQScope) {
        this.controlMQScope = controlMQScope;
    }

    public RichSelectOneChoice getControlMQScope() {
        return controlMQScope;
    }


    public boolean saveDetails() {
        boolean retVal = whodWizardBean.saveDetails();
        // update effected controls
        AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
        adfFacesContext.addPartialTarget(cntrlTrainButtons);
        adfFacesContext.partialUpdateNotify(cntrlTrainButtons);
        adfFacesContext.addPartialTarget(cntrlTrain);
        adfFacesContext.partialUpdateNotify(cntrlTrain);
        return retVal;
    }


    private void setCurrentDetailValuesFromUI() {
        /*
        List selected = (List)whodWizardBean.getProductListControl().getValue();
        if (selected != null) {
            String temp = "";
            for (Object s : selected)
                temp = temp + s + ",";

            temp.replace("[", "");
            temp.replace("]", "");

            if (temp != null & temp.length() > 0)
                whodWizardBean.setCurrentProduct(temp.substring(0, temp.length() - 1));
        }

        List selected = (List)whodWizardBean.getControlMQGroup().getValue();
        if (selected != null) {
            String temp = "";
            for (Object s : selected)
                temp = temp + s + CSMQBean.DEFAULT_DELIMETER_CHAR;

            temp.replace("[", "");
            temp.replace("]", "");
            if (temp != null & temp.length() > 0)
                whodWizardBean.setCurrentMQGROUP(temp.substring(0, temp.length() - 1));
        }
        */
    }

    public void setCurrentInfNoteslValuesFromUI(ActionEvent actionEvent) {
        whodWizardBean.setCurrentInfNoteDescription(String.valueOf(controlInfNoteMQDescription.getValue()));
        whodWizardBean.setCurrentInfNoteSource(String.valueOf(controlInfNoteMQSource.getValue()));
        whodWizardBean.setCurrentInfNoteNotes(String.valueOf(controlInfNoteMQNotes.getValue()));
    }


    public void getDictionaryInfo() {

        whodWizardBean.getDictionaryInfo();

        AdfFacesContext.getCurrentInstance().addPartialTarget(this.controlCurrentTermName);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(this.controlCurrentTermName);
    }


    public void clearRelations() {
        whodWizardBean.clearRelations();

    }


    public String updateRelations() {
        System.out.println("************* REFRESHING RELATIONS ****************");

        whodWizardBean.updateRelations();
        boolean scope = false;
        if (whodWizardBean.getCurrentScope() != null)
            scope = whodWizardBean.getCurrentScope().equals(CSMQBean.HAS_SCOPE);
        termHierarchyBean.init(scope);
        if (termHierarchyBean.getTargetTree() != null) {
            AdfFacesContext.getCurrentInstance().addPartialTarget(termHierarchyBean.getTargetTree());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(termHierarchyBean.getTargetTree());
        }
        return null;
    }


    public void MQDescriptionChanged(ValueChangeEvent valueChangeEvent) {
        controlInfNoteMQDescription.setChanged(true);
    }

    public void dictionaryChanaged(ValueChangeEvent valueChangeEvent) {
        CSMQBean.logger.info(userBean.getCaller() + " dictionaryChanaged:" + valueChangeEvent);
    }

    public void releaseGroupChanged(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent.getNewValue() == null)
            return;
        CSMQBean.logger.info(userBean.getCaller() + " releaseGroupChanged:" + valueChangeEvent);
        whodWizardBean.setCurrentPredictGroups(String.valueOf(controlReleaseGroup.getValue()));
    }

    public void nmqGroupChanged(ValueChangeEvent valueChangeEvent) {
        CSMQBean.logger.info(userBean.getCaller() + " nmqGroupChanged:" + valueChangeEvent);
        whodWizardBean.setCurrentMQGROUP(String.valueOf(whodWizardBean.getControlMQGroup().getValue()));
    }

    public void setControlMQName(RichInputText controlMQName) {
        this.controlMQName = controlMQName;
    }

    public RichInputText getControlMQName() {
        return controlMQName;
    }

    public void setControlInfNoteMQSource(RichInputText controlInfNoteMQSource) {
        this.controlInfNoteMQSource = controlInfNoteMQSource;
    }

    public RichInputText getControlInfNoteMQSource() {
        return controlInfNoteMQSource;
    }


    public void setControlNMQCode(RichInputText controlNMQCode) {
        this.controlNMQCode = controlNMQCode;
    }

    public RichInputText getControlNMQCode() {
        return controlNMQCode;
    }


    public void setControlWizardStatus(RichOutputFormatted controlWizardStatus) {
        this.controlWizardStatus = controlWizardStatus;
    }

    public RichOutputFormatted getControlWizardStatus() {
        return controlWizardStatus;
    }


    public void saveDetailsEvent(ActionEvent actionEvent) {
        this.saveDetails();
    }

    public void setCtrlActiveDictionary(RichInputText controlCurrentBaseDictionary) {
        this.ctrlActiveDictionary = controlCurrentBaseDictionary;
    }

    public RichInputText getCtrlActiveDictionary() {
        return ctrlActiveDictionary;
    }

    public void setControlCurrentTermName(RichInputText controlCurrentTermName) {
        this.controlCurrentTermName = controlCurrentTermName;
    }

    public RichInputText getControlCurrentTermName() {
        return controlCurrentTermName;
    }


    public void setControlRequestor(RichInputText controlRequestor) {
        this.controlRequestor = controlRequestor;
    }

    public RichInputText getControlRequestor() {
        return controlRequestor;
    }

    public void setCntrlTrain(RichTrain cntrlTrain) {
        this.cntrlTrain = cntrlTrain;
    }

    public RichTrain getCntrlTrain() {
        return cntrlTrain;
    }


    public void setCntrlTrainButtons(RichTrainButtonBar cntrlTrainButtons) {
        this.cntrlTrainButtons = cntrlTrainButtons;
    }

    public RichTrainButtonBar getCntrlTrainButtons() {
        return cntrlTrainButtons;
    }

    public void reactivate(DialogEvent dialogEvent) {
        if (WhodUtils.activate(whodWizardBean.getCurrentDictContentID(), whodWizardBean.getCurrentContentCode(),
                               userBean.getUserRole(), userBean.getCurrentUser())) {
            whodWizardBean.setCurrentMQStatus(CSMQBean.ACTIVE_ACTIVITY_STATUS);
            whodWizardBean.setCurrentStatus(CSMQBean.PENDING_RELEASE_STATUS);
            whodWizardBean.setCurrentState(CSMQBean.STATE_DRAFT);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmToolbar);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmToolbar);
        }
    }

    public void retire(DialogEvent dialogEvent) {
        if (WhodUtils.retire(whodWizardBean.getCurrentDictContentID(), whodWizardBean.getCurrentContentCode(),
                             userBean.getUserRole(), userBean.getCurrentUser())) {
            whodWizardBean.setCurrentMQStatus(CSMQBean.INACTIVE_ACTIVITY_STATUS);
            whodWizardBean.setCurrentStatus(CSMQBean.PENDING_RELEASE_STATUS);
            whodWizardBean.setCurrentState(CSMQBean.STATE_DRAFT);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmToolbar);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmToolbar);
        }
    }

    public void delete(DialogEvent dialogEvent) {
        if (WhodUtils.delete(whodWizardBean.getCurrentDictContentID(), whodWizardBean.getCurrentPredictGroups())) {
            whodWizardBean.setActionDelete(true);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmToolbar);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmToolbar);
        }
    }

    public void demoteToDraft(DialogEvent dialogEvent) {
        Hashtable result =
            WhodUtils.changeState(whodWizardBean.getCurrentDictContentID(), CSMQBean.STATE_DRAFT, userBean.getCurrentUser(),
                                  userBean.getUserRole(), whodWizardBean.getCurrentRequestedByDate(), null,
                                  cSMQBean.getDefaultDraftReleaseGroup());
        if (result != null) {
            whodWizardBean.setCurrentPredictGroups(cSMQBean.getDefaultDraftReleaseGroup());
            whodWizardBean.setCurrentState((String)result.get("STATE"));
            whodWizardBean.setCurrentReasonForApproval((String)result.get("REASON"));
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmToolbar);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmToolbar);
        }
    }

    public void confirmReviewed(DialogEvent dialogEvent) {
        Hashtable result =
            WhodUtils.changeState(whodWizardBean.getCurrentDictContentID(), CSMQBean.STATE_REVIEWED, userBean.getCurrentUser(),
                                  userBean.getUserRole(), whodWizardBean.getCurrentRequestedByDate(), null,
                                  cSMQBean.getDefaultDraftReleaseGroup());
        if (result != null) {
            whodWizardBean.setCurrentPredictGroups(cSMQBean.getDefaultDraftReleaseGroup());
            whodWizardBean.setCurrentState((String)result.get("STATE"));
            whodWizardBean.setCurrentReasonForApproval((String)result.get("REASON"));
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmToolbar);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmToolbar);
        }
    }

    public void approve(DialogEvent dialogEvent) {
        Hashtable result =
            WhodUtils.changeState(whodWizardBean.getCurrentDictContentID(), CSMQBean.STATE_APPROVED, userBean.getCurrentUser(),
                                  userBean.getUserRole(), whodWizardBean.getCurrentRequestedByDate(), null,
                                  cSMQBean.getDefaultDraftReleaseGroup());
        if (result != null) {
            whodWizardBean.setCurrentPredictGroups(cSMQBean.getDefaultDraftReleaseGroup());
            whodWizardBean.setCurrentState((String)result.get("STATE"));
            whodWizardBean.setCurrentReasonForApproval((String)result.get("REASON"));
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmToolbar);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmToolbar);
        }
    }

    public void promoteToRequested(DialogEvent dialogEvent) {
        if (whodWizardBean.getCurrentReasonForRequest() == null ||
            whodWizardBean.getCurrentReasonForRequest().length() < 1) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to submit to MQM", "A request reason is required.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        Hashtable result =
            WhodUtils.changeState(whodWizardBean.getCurrentDictContentID(), CSMQBean.STATE_REQUESTED, userBean.getCurrentUser(),
                                  userBean.getUserRole(), whodWizardBean.getCurrentRequestedByDate(),
                                  whodWizardBean.getCurrentReasonForRequest(), cSMQBean.getDefaultDraftReleaseGroup());
        if (result != null) {
            whodWizardBean.setCurrentPredictGroups(cSMQBean.getDefaultDraftReleaseGroup());
            whodWizardBean.setCurrentState((String)result.get("STATE"));
            whodWizardBean.setCurrentReasonForApproval((String)result.get("REASON"));
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmDetailsPanel);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlConfirmToolbar);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlConfirmToolbar);
        }
    }


    public void setCntrlReasonForRequest(RichInputText cntrlReasonForRequest) {
        this.cntrlReasonForRequest = cntrlReasonForRequest;
    }

    public RichInputText getCntrlReasonForRequest() {
        return cntrlReasonForRequest;
    }

    public void setCurrentDateRequested(oracle.jbo.domain.Date currentDateRequested) {
        this.currentDateRequested = currentDateRequested;
    }

    public oracle.jbo.domain.Date getCurrentDateRequested() {
        return currentDateRequested;
    }

    public void setCntrlDateRequested(RichInputDate cntrlDateRequested) {
        this.cntrlDateRequested = cntrlDateRequested;
    }

    public RichInputDate getCntrlDateRequested() {
        return cntrlDateRequested;
    }


    public void setCurrentRequestedByDate(oracle.jbo.domain.Date currentRequestedByDate) {
        this.currentRequestedByDate = currentRequestedByDate;
    }

    public oracle.jbo.domain.Date getCurrentRequestedByDate() {
        return currentRequestedByDate;
    }

    public void setCntrlApproved(RichCommandButton cntrlApproved) {
        this.cntrlApproved = cntrlApproved;
    }

    public RichCommandButton getCntrlApproved() {
        return cntrlApproved;
    }

    public void setCntrlReviewed(RichCommandButton cntrlReviewed) {
        this.cntrlReviewed = cntrlReviewed;
    }

    public RichCommandButton getCntrlReviewed() {
        return cntrlReviewed;
    }


    public void setCntrlConfirmDetailsPanel(RichPanelGroupLayout cntrlConfirmDetailsPanel) {
        this.cntrlConfirmDetailsPanel = cntrlConfirmDetailsPanel;
    }

    public RichPanelGroupLayout getCntrlConfirmDetailsPanel() {
        return cntrlConfirmDetailsPanel;
    }

    public void setCntrlConfirmToolbar(RichToolbar cntrlConfirmToolbar) {
        this.cntrlConfirmToolbar = cntrlConfirmToolbar;
    }

    public RichToolbar getCntrlConfirmToolbar() {
        return cntrlConfirmToolbar;
    }

    public void hierarchyScopeChanged(ValueChangeEvent valueChangeEvent) {
        updateRelations();
    }

    public void setCntrlScope(RichSelectOneChoice cntrlScope) {
        this.cntrlScope = cntrlScope;
    }

    public RichSelectOneChoice getCntrlScope() {
        return cntrlScope;
    }


    public void sortChanged(ValueChangeEvent valueChangeEvent) {
        whodWizardBean.setHierarchyParamSort(valueChangeEvent.getNewValue().toString());
        updateRelations();
    }

    public void showSecondaryPathChanged(ValueChangeEvent valueChangeEvent) {
        whodWizardBean.setParamPrimLinkFlag((Boolean)valueChangeEvent.getNewValue() ? CSMQBean.TRUE : CSMQBean.FALSE);
        updateRelations();
    }

    public void scopeChanged(ValueChangeEvent valueChangeEvent) {
        String newVal = valueChangeEvent.getNewValue().toString();
        //nMQWizardBean.setHierarchyParamScope(newVal);


        termHierarchyBean.setHasScope(newVal.contains(CSMQBean.HAS_SCOPE));
        whodWizardBean.setCurrentScope(newVal.contains(CSMQBean.HAS_SCOPE) ? CSMQBean.HAS_SCOPE : CSMQBean.FALSE);
        //updateRelations();
    }

    public void maxLevelsChanged(ValueChangeEvent valueChangeEvent) {
        whodWizardBean.setMaxLevels(Integer.parseInt(valueChangeEvent.getNewValue().toString()));
        updateRelations();
    }


    public void refreshHierarchy(ActionEvent actionEvent) {
        updateRelations();
    }

    public void setWhodWizardBean(WhodWizardBean nMQWizardBean) {
        this.whodWizardBean = nMQWizardBean;
    }

    public WhodWizardBean getWhodWizardBean() {
        return whodWizardBean;
    }

    public void addRelationsPageLoad(PhaseEvent phaseEvent) {
        System.out.println("Starts exec WhodWizardUIBean.addRelationsPageLoad() PhaseId = " + phaseEvent.getPhaseId());
        if (phaseEvent.getPhaseId().equals(PhaseId.RENDER_RESPONSE)) {
            try {
                if (!whodWizardBean.isTreeAccessed()) {
                    updateRelations();
                    whodWizardBean.setTreeAccessed(true);
                }
                //if (getCntrlIncludeLLTsInExport() != null)
                //     this.getCntrlIncludeLLTsInExport().setValue(false);
                whodWizardBean.setIncludeLLTsInExport(false);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception in WhodWizardUIBean.addRelationsPageLoad() is " + e);
            }
        }
    }


    public void setCntrlIncludeLLTsInExport(RichSelectBooleanCheckbox cntrlIncludeLLTsInExport) {
        this.cntrlIncludeLLTsInExport = cntrlIncludeLLTsInExport;
    }

    public RichSelectBooleanCheckbox getCntrlIncludeLLTsInExport() {
        return cntrlIncludeLLTsInExport;
    }

    public void setCntrlExportDisplayedOnly(RichSelectBooleanCheckbox cntrlExportDisplayedOnly) {
        this.cntrlExportDisplayedOnly = cntrlExportDisplayedOnly;
    }

    public RichSelectBooleanCheckbox getCntrlExportDisplayedOnly() {
        return cntrlExportDisplayedOnly;
    }

    public void addRelationsPageBeforeLoad(PhaseEvent phaseEvent) {
        whodWizardBean.getDictionaryInfo(); // Add event code here...
    }

    public void requestedByDateChanged(ValueChangeEvent valueChangeEvent) {
        Date reqDate = (Date)valueChangeEvent.getNewValue();
        whodWizardBean.setCurrentDateRequested(reqDate);
        // Add event code here...
    }

    public void extensionChanged(ValueChangeEvent valueChangeEvent) {

        String newExt = valueChangeEvent.getNewValue().toString();
        whodWizardBean.setIsNMQ(newExt.equals("NMQ"));
        whodWizardBean.setIsSMQ(newExt.equals("SMQ"));


        whodWizardBean.setCurrentMQType(newExt);
        whodWizardBean.loadProductSelectList();

        if (whodWizardBean.getCurrentMQType().equals(CSMQBean.NMQ) ||
            whodWizardBean.getCurrentMQType().equals(CSMQBean.SMQ))
            controlMQLevel.setReadOnly(false);
        else
            controlMQLevel.setReadOnly(true);


        //   if (!nMQWizardBean.isIsNMQ() && (userBean.isMQM() || userBean.isRequestor())) nMQWizardBean.setCurrentState(CSMQBean.STATE_DRAFT);
        //  if (nMQWizardBean.isIsNMQ()  && userBean.isRequestor()) nMQWizardBean.setCurrentState(CSMQBean.STATE_PROPOSED);

        if (whodWizardBean.isIsNMQ() && userBean.isRequestor() && !userBean.isMQM()) {
            whodWizardBean.setCurrentState(CSMQBean.STATE_PROPOSED);
        } else {
            whodWizardBean.setCurrentState(CSMQBean.STATE_DRAFT);
        }
        // FIX: 20141121-2
        if (whodWizardBean.isIsNMQ())
            whodWizardBean.getProductList().add(CSMQBean.DEFAULT_PRODUCT);


        AdfFacesContext.getCurrentInstance().addPartialTarget(controlMQState);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(controlMQState);

        AdfFacesContext.getCurrentInstance().addPartialTarget(whodWizardBean.getControlDesignee());
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(whodWizardBean.getControlDesignee());

        AdfFacesContext.getCurrentInstance().addPartialTarget(whodWizardBean.getProductListControl());
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(whodWizardBean.getProductListControl());

        AdfFacesContext.getCurrentInstance().addPartialTarget(controlMQLevel);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(controlMQLevel);

    }

    public void addDetailsPageLoad(PhaseEvent phaseEvent) {
        whodWizardBean.loadProductSelectList();
    }

    public void setCntrlProductSelectItems(UISelectItems cntrlProductSelectItems) {
        this.cntrlProductSelectItems = cntrlProductSelectItems;
    }

    public UISelectItems getCntrlProductSelectItems() {
        return cntrlProductSelectItems;
    }

    public String getName() {
        return null;
    }

    public void release() {
    }

    public Object getDataProvider() {
        return null;
    }

    public boolean invokeOperation(Map p0, OperationBinding p1) {
        return false;
    }

    public boolean isTransactionDirty() {
        return false;
    }

    public void rollbackTransaction() {
    }

    public void commitTransaction() {
    }

    public boolean setAttributeValue(AttributeContext p0, Object p1) {
        return false;
    }

    public Object createRowData(RowContext p0) {
        return null;
    }

    public Object registerDataProvider(RowContext p0) {
        return null;
    }

    public boolean removeRowData(RowContext p0) {
        return false;
    }

    public void validate() {
    }

    public void beginRequest(HashMap p0) {
    }

    public void endRequest(HashMap p0) {
    }

    public boolean resetState() {
        return false;
    }
}

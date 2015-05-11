package com.dbms.csmq.view;

import com.dbms.csmq.view.hierarchy.WhodTermHierarchyBean;

import oracle.adf.view.rich.context.AdfFacesContext;

public class WhodAddRelationsRenderingRulesBean extends WhodRenderingRulesBean{
    WhodTermHierarchyBean termHierarchyBean = (WhodTermHierarchyBean) AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodTermHierarchyBean");
    private boolean wizardRelationsDisableDetails = false;
    

    public WhodAddRelationsRenderingRulesBean() {
        super();
    }

    public void setWizardRelationsDisableDetails(boolean wizardRelationsDisableDetails) {
        this.wizardRelationsDisableDetails = wizardRelationsDisableDetails;
    }

    public boolean isWizardRelationsDisableDetails() {
        if (!termHierarchyBean.isHasScope() || !super.isWizardRelationsRenderSave()) wizardRelationsDisableDetails = true;
        return wizardRelationsDisableDetails;
    }
}

package com.dbms.csmq.view;

import com.dbms.csmq.view.impact.WhodFutureImpactHierarchyBean;

import oracle.adf.view.rich.context.AdfFacesContext;

public class WhodImpactAssessmentRenderingRulesBean extends WhodRenderingRulesBean{
    
    WhodFutureImpactHierarchyBean futureImpactHierarchyBean = (WhodFutureImpactHierarchyBean) AdfFacesContext.getCurrentInstance().getPageFlowScope().get("FutureImpactHierarchyBean");
    private boolean wizardRelationsDisableDetails = false;
       
    
    public WhodImpactAssessmentRenderingRulesBean() {
        super();
    }

    public void setWizardRelationsDisableDetails(boolean wizardRelationsDisableDetails) {
        this.wizardRelationsDisableDetails = wizardRelationsDisableDetails;
    }

    public boolean isWizardRelationsDisableDetails() {
        if (!futureImpactHierarchyBean.isHasScope() || !futureImpactHierarchyBean.isEditable()) wizardRelationsDisableDetails = true;
        return wizardRelationsDisableDetails;
    }
}

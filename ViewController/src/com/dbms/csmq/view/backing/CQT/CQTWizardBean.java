package com.dbms.csmq.view.backing.CQT;

import java.util.Map;

import oracle.adf.view.rich.context.AdfFacesContext;

public class CQTWizardBean {
    public CQTWizardBean() {
    }

    public String setMode() {
        AdfFacesContext adfctx = null;
           adfctx = AdfFacesContext.getCurrentInstance();

           Map pageParams = adfctx.getPageFlowScope();
           
           System.out.println("----Passed Value----"+pageParams.get("setMode"));
           
           return null;
    }
}

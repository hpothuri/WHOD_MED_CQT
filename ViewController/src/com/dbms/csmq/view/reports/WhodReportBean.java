package com.dbms.csmq.view.reports;


import com.dbms.csmq.CSMQBean;
import com.dbms.csmq.UserBean;
import com.dbms.csmq.view.backing.whod.WhodUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import javax.naming.InitialContext;

import javax.sql.DataSource;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.input.RichInputDate;
import oracle.adf.view.rich.component.rich.input.RichSelectBooleanCheckbox;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.component.rich.nav.RichCommandButton;
import oracle.adf.view.rich.component.rich.nav.RichGoButton;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.Row;


public class WhodReportBean {

    private RichSelectOneChoice cntrlReportList;
    private RichSelectOneChoice cntrlFormatList;
    private RichInputDate cntrlStartDate;
    private RichInputDate cntrlEndDate;

    private String reportName;
    private String reportFormat = "PDF";
    private String paramRequestor = "NULL";
    private String paramReleaseGroup = "NULL";
    private Date paramStartDate = null;
    private Date paramEndDate = null;
    private oracle.jbo.domain.Date paramLastActivationDate = null;
    public static final String GROUP_DELIMETER = ",";
    private RichGoButton cntrlOpenReport;
    private String reportURL;
    private RichSelectBooleanCheckbox cntrlReactivated;
    private ArrayList<SelectItem> releaseGroupSelectItems;
    private CSMQBean cSMQBean;
    private UserBean userBean;
    private String generatedReport;
    private RichCommandButton generateReport;

    public WhodReportBean() {
        cSMQBean = (CSMQBean)ADFContext.getCurrent().getApplicationScope().get("CSMQBean");
        releaseGroupSelectItems = cSMQBean.getAGsForDictionary(null);
        userBean = (UserBean)ADFContext.getCurrent().getSessionScope().get("UserBean");
        userBean.setCurrentMenuPath("Reports");
        userBean.setCurrentMenu("CQT_REPORTS");
    }

    public void printReport(FacesContext facesContext, OutputStream outputStream) {

        reportName = cntrlReportList.getValue().toString();
        reportFormat = cntrlFormatList.getValue().toString();
        String sourceDirectory = CSMQBean.getWhodProperty("REPORT_SOURCE");
        sourceDirectory = "F:\\mine\\CQT\\docs\\rewhodcqtreports\\"; //TODO need to remove before commit
        String reportFile = sourceDirectory + reportName + ".jrxml";

        if (cntrlStartDate.getValue() != null) {
            paramStartDate = (Date)cntrlStartDate.getValue();
        } else {
            paramStartDate = WhodUtils.getDate("01-JAN-1800", "dd-MMM-yy");
        }

        if (cntrlEndDate.getValue() != null) {
            paramEndDate = (Date)cntrlEndDate.getValue();
        } else {
            paramEndDate = WhodUtils.getDate("15-AUG-3501", "dd-MMM-yy");
        }

        Map parameters = new HashMap();
        parameters.put("trans_start_date", paramStartDate);
        parameters.put("trans_end_date", paramEndDate);
        parameters.put("REPORT_DIRECTORY", sourceDirectory);
        parameters.put("ReportTitle", reportName);

        CSMQBean.logger.info(userBean.getCaller() + " *** RUNNING REPORT ***");
        CSMQBean.logger.info(userBean.getCaller() + " trans_start_date: " + paramStartDate);
        CSMQBean.logger.info(userBean.getCaller() + " trans_end_date: " + paramEndDate);

        try {
            InitialContext initialContext = new InitialContext();
            //DataSource ds = (DataSource)initialContext.lookup(CSMQBean.getProperty("DATABASE_URL")); // get from your application module configuration
            DataSource ds = (DataSource)initialContext.lookup("jdbc/WHODDS"); //TODO need to remove before commit
            Connection conn = ds.getConnection();
            InputStream is = new FileInputStream(new File(reportFile));
            JasperDesign jasperDesign = JRXmlLoader.load(is);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

            if (reportFormat.equals("PDF")) {
                JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            } else if (reportFormat.equals("XLS")) {
                JRXlsExporter exporterXLS = new JRXlsExporter();
                exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
                exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outputStream);
                exporterXLS.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
                                         Boolean.TRUE);
                exporterXLS.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS,
                                         Boolean.TRUE);
                exporterXLS.exportReport();
            }

            is.close();
            conn.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setCntrlReportList(RichSelectOneChoice cntrlReportList) {
        this.cntrlReportList = cntrlReportList;
    }

    public RichSelectOneChoice getCntrlReportList() {
        return cntrlReportList;
    }

    public void setCntrlFormatList(RichSelectOneChoice cntrlFormatList) {
        this.cntrlFormatList = cntrlFormatList;
    }

    public RichSelectOneChoice getCntrlFormatList() {
        return cntrlFormatList;
    }

    public void setCntrlStartDate(RichInputDate startDate) {
        this.cntrlStartDate = startDate;
    }

    public RichInputDate getCntrlStartDate() {
        return cntrlStartDate;
    }

    public void setCntrlEndDate(RichInputDate endDate) {
        this.cntrlEndDate = endDate;
    }

    public RichInputDate getCntrlEndDate() {
        return cntrlEndDate;
    }

    public void setParamRequestor(String paramRequestor) {
        this.paramRequestor = paramRequestor;
    }

    public String getParamRequestor() {
        return paramRequestor;
    }

    public void setParamReleaseGroup(String paramReleaseGroup) {
        this.paramReleaseGroup = paramReleaseGroup;
    }

    public String getParamReleaseGroup() {
        return paramReleaseGroup;
    }

    public void setParamStartDate(Date paramStartDate) {
        this.paramStartDate = paramStartDate;
    }

    public Date getParamStartDate() {
        return paramStartDate;
    }

    public void setParamEndDate(Date paramEndDate) {
        this.paramEndDate = paramEndDate;
    }

    public Date getParamEndDate() {
        return paramEndDate;
    }

    public void setCntrlOpenReport(RichGoButton cntrlOpenReport) {
        this.cntrlOpenReport = cntrlOpenReport;
    }

    public RichGoButton getCntrlOpenReport() {
        return cntrlOpenReport;
    }

    public void setReportURL(String reportURL) {
        this.reportURL = reportURL;
    }

    public String getReportURL() {
        return reportURL;
    }

    public void setCntrlReactivated(RichSelectBooleanCheckbox cntrlReactivated) {
        this.cntrlReactivated = cntrlReactivated;
    }

    public RichSelectBooleanCheckbox getCntrlReactivated() {
        return cntrlReactivated;
    }

    public void setParamLastActivationDate(oracle.jbo.domain.Date paramLastActivationDate) {
        this.paramLastActivationDate = paramLastActivationDate;
    }

    public java.util.Date getParamLastActivationDate() {
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("ViewObjLatestActivationState1Iterator");

        Enumeration rows = dciterb.getRowSetIterator().enumerateRowsInRange();
        Row row = (Row)rows.nextElement();
        Object tVal = row.getAttribute("LastActivationDate");
        if (tVal == null)
            return null;
        oracle.jbo.domain.Date paramLastActivationDate = (oracle.jbo.domain.Date)tVal;
        return paramLastActivationDate.dateValue();
    }

    public void setGeneratedReport(String generatedReport) {
        this.generatedReport = generatedReport;
    }

    public String getGeneratedReport() {
        reportName = cntrlReportList.getValue().toString();
        reportFormat = cntrlFormatList.getValue().toString();
        generatedReport = reportName + "." + reportFormat;
        return generatedReport;
    }

    public void formatChanged(ValueChangeEvent valueChangeEvent) {
        getGeneratedReport();
        AdfFacesContext.getCurrentInstance().addPartialTarget(this.generateReport);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(this.generateReport);
    }

    public void setGenerateReport(RichCommandButton generateReport) {
        this.generateReport = generateReport;
    }

    public RichCommandButton getGenerateReport() {
        return generateReport;
    }

    public void setReportFormat(String reportFormat) {
        this.reportFormat = reportFormat;
    }

    public String getReportFormat() {
        return reportFormat;
    }

    public void reportNameChanged(ValueChangeEvent valueChangeEvent) {
        //this.fileName = valueChangeEvent.getNewValue().toString();
    }
}

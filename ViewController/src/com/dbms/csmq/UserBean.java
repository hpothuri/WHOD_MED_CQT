package com.dbms.csmq;


import com.dbms.util.Utils;
import com.dbms.util.dml.DMLUtils;

import java.io.IOException;

import java.security.Principal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.application.ViewExpiredException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

import javax.security.auth.Subject;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.adf.controller.ControllerContext;
import oracle.adf.share.ADFContext;
import oracle.adf.share.security.SecurityContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.nav.RichCommandButton;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.PopupCanceledEvent;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.server.DBTransaction;

import oracle.security.idm.IdentityStore;
import oracle.security.idm.Role;
import oracle.security.idm.RoleManager;
import oracle.security.idm.User;
import oracle.security.idm.UserManager;
import oracle.security.idm.UserProfile;
import oracle.security.jps.JpsContext;
import oracle.security.jps.JpsContextFactory;
import oracle.security.jps.service.idstore.IdentityStoreService;

import org.apache.log4j.Logger;

import weblogic.security.Security;
import weblogic.security.URLCallbackHandler;
import weblogic.security.services.Authentication;
import weblogic.security.spi.WLSGroup;
import weblogic.security.spi.WLSUser;


public class UserBean {

    // Session scoped bean
    
    private RichCommandButton cntrlChangePasswordButton;
    private RichPopup cntrlChangePasswordPopup;
    private RichPopup cntrlAlertExpiryPopup;
    private ArrayList<String> roles = new ArrayList<String>();
    Logger accessLogger = Logger.getLogger("accessLogger");
    private String remoteAddress = "";
    private String caller = "Anonymous";
    private Hashtable mQHistory;


    public UserBean() {
        System.setProperty("wls.propagate.login.exception.cause", "true");
        mQHistory = new Hashtable(CSMQBean.HISTORY_LENGTH);
        // set app defaults for user
    }

    private boolean loggedIn = false;
    private String username = "";
    private String password = "";
    private String newPassword = "";
    private String repeatPassword = "";
    private String currentUser = "";
    private String currentMenu = "";
    private String currentMenuPath = "";
    private String firstName = "";
    private String lastName = "";
    private String fullName = "";

    private boolean MQM = false;
    private boolean requestor = false;
    private boolean admin = false;

    private int passwordExpiryDays;
    private boolean showExpiryPopup = true;

    public void setUsername(String username) {
        this.username = username.toLowerCase();
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void clear() {
        this.currentMenu = "";
        this.currentMenuPath = "";
    }

    public String doReadOnlyLogin() {
        this.username = "Browse";
        this.password = "read_only";
        return doLogin();
    }


    public String checkPWExpiry() {
        int daysToExperiry = getPasswordExpiry();
        FacesMessage msg =
            new FacesMessage(FacesMessage.SEVERITY_WARN, "Password Expiration Warning", "Your password will expire in " +
                             daysToExperiry + " days.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return null;
    }

    public String doLogin() {
        
        if (username == null || username.length() < 1) return null;
        if (password == null || password.length() < 1) return null;
        
        Subject subject = null;
        String un = username;
        byte[] pw = password.getBytes();
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)ctx.getExternalContext().getRequest();
        remoteAddress = request.getRemoteAddr();
        SecurityContext s;

        accessLogger.info("LOGIN ATTEPT FOR USER: " + un + " FROM: " + remoteAddress);

        try {
            subject = Authentication.login(new URLCallbackHandler(un, pw));
            weblogic.servlet.security.ServletAuthentication.runAs(subject, request);

            String loginUrl = "/adfAuthentication?success_url=/faces/index.jspx";
            HttpServletResponse response = (HttpServletResponse)ctx.getExternalContext().getResponse();
            sendForward(request, response, loginUrl);
            
            this.passwordExpiryDays = getPasswordExpiry();
            this.showExpiryPopup =
                    (passwordExpiryDays <= CSMQBean.SHOW_PASSWORD_WARNING_CUTOFF && passwordExpiryDays !=
                     CSMQBean.NULL_PASSWORD_EXPIRATION); // only show the popup if it is less than the cutoff

            if (showExpiryPopup)
                accessLogger.info("PASSWORD WILL EXPIRE IN: " + passwordExpiryDays + " DAYS");
            
            ADFContext adfContext = ADFContext.getCurrent();
            SecurityContext securityCtx = adfContext.getSecurityContext();

            Subject subject2 = Security.getCurrentSubject();

            StringBuffer groups = new StringBuffer();
            String user = null;
            boolean first = true;

            for (Principal p : subject2.getPrincipals()) {
                if (p instanceof WLSGroup) {
                    if (first) {
                        first = false;
                    } else {
                        groups.append(", ");
                    }
                    groups.append(p.getName());
                } else if (p instanceof WLSUser) {
                    user = p.getName();
                }
            }

            setName();
            loggedIn = true;
            accessLogger.info("ROLES: " + groups);

            caller = "[" + remoteAddress + ":" + username + "] ";
        } catch (FailedLoginException fle) {
            accessLogger.info("FAILED LOGIN: " + un);
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Incorrect Username or Password", "An incorrect Username or Password was specified");
            ctx.addMessage(null, msg);
            return null;
        } catch (AccountLockedException ale) {
            accessLogger.info("ACCOUNT LOCKED: " + un);
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Account locked", "This account is locked.  Please contact NMAT support to reset your password.");
            ctx.addMessage(null, msg);
            return null;
        } catch (AccountExpiredException aee) {
            accessLogger.info("PASSWORD EXPIRED: " + un);
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Account locked", "This password has expired.  Please contact NMAT support to reset your password.");
            ctx.addMessage(null, msg);
            return null;
        } catch (LoginException le) {
            reportUnexpectedLoginError("LoginException", le);
            return null;
        } catch (Exception e) {
            CSMQBean.logger.error("ERROR", e);
            return null;
        }
        accessLogger.info("SUCCESSFUL LOGIN: " + un);
              
        return null;
    }


    private void setName (){
        
        String sql = "select last_name, first_name from tms.tms_user_accounts where oa_account_name = '"+username+"'";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        
        CallableStatement cstmt = dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT);
            
        System.out.println("***** : " + sql);
        try {
            rs = cstmt.executeQuery();
            if (rs.next()) {
                this.lastName =  rs.getString("last_name");
                this.firstName = rs.getString("first_name");
                }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }


    public String changePassword() {
        accessLogger.info("Password change for user: " + this.username);
        SecurityContext securityContext = ADFContext.getCurrent().getSecurityContext();
        String userName = securityContext.getUserName();
        FacesMessage msg = null;

        if (!newPassword.equals(repeatPassword)) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Change Password", "The passwords do not match.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }

        JpsContextFactory ctxf;
        try {
            ctxf = JpsContextFactory.getContextFactory();
            JpsContext ctx = ctxf.getContext();
            IdentityStoreService storeService = ctx.getServiceInstance(IdentityStoreService.class);
            IdentityStore store = storeService.getIdmStore();
            User user = store.searchUser(userName);
            UserProfile userProfile = user.getUserProfile();
            userProfile.setPassword(password.toCharArray(), newPassword.toCharArray());
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Change Password", "Password successfully changed.");
            this.cntrlChangePasswordPopup.cancel();
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private String user = null;

    public String getCurrentUserRoles() {

        String curRoles = "";
        for (String role : roles) {
            curRoles = curRoles + ", " + role;
        }
        return curRoles;
    }

    public String getCurrentUserRole() {
        ADFContext adfContext = ADFContext.getCurrent();
        SecurityContext securityCtx = adfContext.getSecurityContext();
        if (securityCtx.isUserInRole("MQM"))
            return "MQM";
        if (securityCtx.isUserInRole("Requestor"))
            return "Requestor";
        if (securityCtx.isUserInRole("Admin"))
            return "Admin";
        return "User";
    }


    public boolean isWlsUserRole() {
        for (int i = 0; i < roles.size(); i++) {
            if ("users".equalsIgnoreCase(roles.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean isContainerUserRole() {
        if (FacesContext.getCurrentInstance().getExternalContext().isUserInRole("User_role")) {
            return true;
        }
        return false;
    }


    private int getPasswordExpiry() {
        int retVal = CSMQBean.NULL_PASSWORD_EXPIRATION;
        String url = DMLUtils.getJDBCURL();
        String prefix = "jdbc:oracle:thin:";

        url = prefix + url.substring(url.indexOf("@"), url.length());

        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            //Connection conn[] = new Connection[1];
            Properties props = new Properties();
            props.put("user", username);
            props.put("password", password);
            Connection conn;
            
            try {
                conn = DriverManager.getConnection(url, props);
                }
            catch (SQLException sqle) {
                accessLogger.error (sqle.getMessage());
                return retVal;
            }
            
            SQLWarning sqlw = conn.getWarnings();
            accessLogger.info("SQL Warning for user [" + username + "]: " + sqlw);

            // if there is a password error, get the number of days until it expires otherwise don't show the error

            if (sqlw != null && sqlw.getErrorCode() == CSMQBean.PASSWORD_EXPIRY_WARNING) {
                Pattern intsOnly = Pattern.compile("\\d+");
                Matcher makeMatch =
                    intsOnly.matcher(sqlw.toString().substring(31)); // strip out the ORA error so we get the correct int - not the error #
                makeMatch.find();
                String days = makeMatch.group();
                retVal = Integer.parseInt(days);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return retVal;
        }
        return retVal;
    }

    /*
    private int getPassordExpiry (String userID) {


        //FUNCTION nmat_security_pkg.get_days_to_expiry (i_username    IN  dba_role_privs.grantee%TYPE) RETURN NUMBER


        UserBean.logInfo("*** GETTING PASSWORD EXPIRY ***");

        String sql = "{? = call nmat_security_pkg.get_days_to_expiry(?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        CallableStatement cstmt = dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT);

        int days = -11;  // array to return the new state and message

        try {
            cstmt.setString(2, userID);
            //cstmt.setInt("nExpireDays", 0);

            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.execute();

            days = cstmt.getInt(1);
            cstmt.close();
            return days;
            }
        catch (SQLException e) {
            e.printStackTrace();
            return days;
            }
    }
    */


    public void setCurrentMenu(String currentMenu) {
        this.currentMenu = currentMenu;
    }

    public String getCurrentMenu() {
        return currentMenu;
    }

    public void setCurrentMenuPath(String currentMenuPath) {
        this.currentMenuPath = currentMenuPath;
    }

    public String getCurrentMenuPath() {
        return currentMenuPath;
    }


    private void sendForward(HttpServletRequest request, HttpServletResponse response, String forwardUrl) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        RequestDispatcher dispatcher = request.getRequestDispatcher(forwardUrl);
        try {
            dispatcher.forward(request, response);
        } catch (ServletException se) {
            reportUnexpectedLoginError("ServletException", se);
        } catch (IOException ie) {
            reportUnexpectedLoginError("IOException", ie);
        }
        ctx.responseComplete();
    }

    private void reportUnexpectedLoginError(String errType, Exception e) {
        FacesMessage msg =
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unexpected error during login", "Unexpected error during login (" +
                             errType + "), please consult logs for detail");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        e.printStackTrace();
    }

    public void logout() {

        FacesContext fctx = FacesContext.getCurrentInstance();

        ExternalContext ectx = fctx.getExternalContext();

        accessLogger.info("LOGING OFF USER: " + ectx.getRemoteUser());

        String url = ectx.getRequestContextPath() + "/adfAuthentication?logout=true&end_url=/faces/index.jspx";
        try {
            ectx.redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fctx.responseComplete();

    }

    SecurityContext sec = ADFContext.getCurrent().getSecurityContext();


    public void manuallyAddUserToRole() {

        try {
            JpsContextFactory jps = JpsContextFactory.getContextFactory();
            JpsContext jpsContext = jps.getContext();
            IdentityStoreService storeService = jpsContext.getServiceInstance(IdentityStoreService.class);
            IdentityStore is = storeService.getIdmStore();
            UserManager mn = is.getUserManager();
            RoleManager rm = is.getRoleManager();
            Principal p = mn.createUser("ADMIN_TEST_USER", "123456789".toCharArray()).getPrincipal();
            Role r = is.searchRole(is.SEARCH_BY_NAME, "ADMIN_TEST_ROLE");
            rm.grantRole(r, p);
        } catch (Exception e) {
            CSMQBean.logger.info(getCaller() + e.getStackTrace().toString());
        }
    }

    public void setMQM(boolean MQM) {
        this.MQM = MQM;
    }

    public boolean isMQM() {
        this.MQM = isUserInRole(CSMQBean.ROLE_MQM);
        return MQM;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        this.admin = isUserInRole(CSMQBean.ROLE_ADMIN);
        return admin;
    }

    public void setRequestor(boolean requestor) {
        this.requestor = requestor;
    }

    public boolean isRequestor() {
        this.requestor = isUserInRole(CSMQBean.ROLE_REQUESTOR);
        return requestor;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getCurrentUser() {
        this.currentUser = getUsername();
        return currentUser;
    }

    //Convience methods for getting user roles

    public boolean isUserInRole(String role) {
        return sec.isUserInRole(role);
    }

    public String getUserRole() {
        if (isMQM())
            return CSMQBean.ROLE_MQM;
        if (isRequestor())
            return CSMQBean.ROLE_REQUESTOR;
        if (isAdmin())
            return CSMQBean.ROLE_ADMIN;
        return CSMQBean.ROLE_USER;
    }


    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void newPasswordValidator(FacesContext facesContext, UIComponent uIComponent, Object object) {
        if (!newPassword.equals(repeatPassword)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "The passwords do not match.",
                                                          null));
        } else {
            cntrlChangePasswordButton.setDisabled(false);
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlChangePasswordButton);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlChangePasswordButton);
        }
    }

    public void setCntrlChangePasswordButton(RichCommandButton cntrlChangePasswordButton) {
        this.cntrlChangePasswordButton = cntrlChangePasswordButton;
    }

    public RichCommandButton getCntrlChangePasswordButton() {
        return cntrlChangePasswordButton;
    }

    public void newPWChanged(ValueChangeEvent valueChangeEvent) {
        this.newPassword = valueChangeEvent.getNewValue().toString();


    }

    public void repeatPWChanged(ValueChangeEvent valueChangeEvent) {
        this.repeatPassword = valueChangeEvent.getNewValue().toString();


    }


    public void setCntrlChangePasswordPopup(RichPopup cntrlChangePasswordPopup) {
        this.cntrlChangePasswordPopup = cntrlChangePasswordPopup;
    }

    public RichPopup getCntrlChangePasswordPopup() {
        return cntrlChangePasswordPopup;
    }

    public void setPasswordExpiryDays(int passwordExpiryDays) {
        this.passwordExpiryDays = passwordExpiryDays;
    }

    public int getPasswordExpiryDays() {
        return passwordExpiryDays;
    }

    public void setShowExpiryPopup(boolean showExpiryPopup) {
        this.showExpiryPopup = showExpiryPopup;
    }

    public boolean isShowExpiryPopup() {
        return showExpiryPopup;
    }

    public void hideExpiryPopup(ClientEvent clientEvent) {
        showExpiryPopup = false;
    }

    public void showExpiryPopupCancelled(PopupCanceledEvent popupCanceledEvent) {
        showExpiryPopup = false;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getCaller() {
        return caller;
    }

    public void setCntrlAlertExpiryPopup(RichPopup cntrlAlertExpiryPopup) {
        this.cntrlAlertExpiryPopup = cntrlAlertExpiryPopup;
    }

    public RichPopup getCntrlAlertExpiryPopup() {
        return cntrlAlertExpiryPopup;
    }


    public void setMQHistory(Hashtable MQHistory) {
        this.mQHistory = MQHistory;
    }

    public Hashtable getMQHistory() {
        return mQHistory;
    }
    
    public void addHistory (String mQName, String mqCode) {
        mQHistory.put(mqCode, mQName);
        // write it back out to the cookie
        String cookieVal ="";
        for (Object key : mQHistory.keySet()) 
            cookieVal += key + "|" + mQHistory.get(key) + "^";
        Utils.changeCookieValue(CSMQBean.appName, cookieVal);

    } 
    
    public boolean isSessionExpired() {
     Exception e =  ControllerContext.getInstance().getCurrentViewPort().getExceptionData();
        if ( e != null && (e instanceof ViewExpiredException)) {
            return true;
            }
        return false;
     }

    public void setLoggedIn(boolean loggedIn) {
        
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        fullName = lastName + ", " + firstName;        
        return fullName;
    }
}

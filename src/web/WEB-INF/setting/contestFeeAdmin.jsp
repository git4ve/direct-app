<%--
  - Author: TCSASSEMBLER
  - Version: 1.0 (Release Assembly - TopCoder Cockpit Settings Related Pages Refactoring)
  - Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
  -
  - Description: This page renders the contest fee admin setting page.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/includes/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <jsp:include page="/WEB-INF/includes/htmlhead.jsp"/>
    <jsp:include page="/WEB-INF/includes/paginationSetup.jsp"/>

    <ui:dashboardPageType tab="dashboard"/>
</head>

<c:set var="CURRENT_TAB" scope="request" value="settings"/>

<body id="page">

<div id="wrapper">
<div id="wrapperInner">
<div id="container">
<div id="content">

<jsp:include page="/WEB-INF/includes/header.jsp"/>

<div id="mainContent">

<jsp:include page="/WEB-INF/includes/right.jsp"/>
<div id="area1"><!-- the main area -->
    <div class="area1Content">
        <div class="currentPage">
            <a href="<s:url action="dashboardActive" namespace="/"/>" class="home">Dashboard</a> &gt;
            <strong>Settings</strong> &gt;
            <strong>Contest Fee Management</strong>
        </div>

        <div class="areaHeader">
            <h2 class="title contestTitle">Contest Fee Management</h2>
            <div class="select">
                <div class="selectInnerBox">
                    <div class="selectInner">
                        <label for="selectSetting">Select a Setting Panel:</label>
                <span class="settingPanel">
                    <select id="selectSetting" name="select">
                        <option value="notifications">
                            Notifications</option>
                        <option value="permissions">
                            Permissions</option>
                        <s:if test="ContestFeeSettingAccessible">
                            <option selected="selected" value="fee">
                                Contest Fee</option>
                        </s:if>
                        <s:if test="jiraSynAccessible">
                            <option value="sync">
                                Synchronize User in JIRA and WIKI</option>
                        </s:if>
                    </select>
                </span>
                        <div class="clearFix"></div>
                    </div>
                    <span class="corner tl"></span>
                    <span class="corner tr"></span>
                    <span class="corner bl"></span>
                    <span class="corner br"></span>
                </div>
            </div>
        </div>
        <!-- End .areaHeader -->
        <div>
            <p class="billingAccountArchive"><a
                    href='<s:url action="listBillingAccountAction" namespace="/"/>'>Project Contest
                Fees Management for Billing Accounts</a></p>

            <p class="billingAccountArchive"><a
                    href='<s:url action="createContestFeesHome" namespace="/"/>'>Project Contest
                Fees Creation for Billing Accounts</a></p>

            <p class="billingAccountArchive"><a
                    href='<s:url action="listCustomerPlatformFee" namespace="/"/>'>Platform Fee Management for Client</a></p>

            <p class="billingAccountArchive"><a
                    href='<s:url action="enterCreateCustomerPlatformFee" namespace="/"/>'>Platform Fee Creation for Client</a></p>
        </div>
    </div>
</div>

</div>


</div>
<!-- End #mainContent -->
</div>
<!-- End #content -->
<jsp:include page="/WEB-INF/includes/footer.jsp"/>

</div>
<!-- End #container -->
</div>
</div>
<!-- End #wrapper -->

<jsp:include page="/WEB-INF/includes/popups.jsp"/>


</body>
<!-- End #page -->


</html>
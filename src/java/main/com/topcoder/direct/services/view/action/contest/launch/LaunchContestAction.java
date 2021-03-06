/*
 * Copyright (C) 2010  -2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.services.view.action.contest.launch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.topcoder.direct.services.project.milestone.model.Milestone;
import com.topcoder.direct.services.project.milestone.model.MilestoneStatus;
import com.topcoder.direct.services.project.milestone.model.SortOrder;
import com.topcoder.direct.services.view.dto.IdNamePair;
import com.topcoder.direct.services.view.dto.contest.ContestCopilotDTO;
import com.topcoder.direct.services.view.util.DirectUtils;
import org.apache.struts2.ServletActionContext;

import com.topcoder.direct.services.view.dto.CommonDTO;
import com.topcoder.direct.services.view.dto.UserProjectsDTO;
import com.topcoder.direct.services.view.dto.project.ProjectBriefDTO;
import com.topcoder.direct.services.view.util.DataProvider;
import com.topcoder.direct.services.view.util.SessionData;
import com.topcoder.security.TCSubject;

/**
 * <p>
 * The launch contest action. It will trigger to load the launch contest page and provide getters for some data.
 * </p>
 * <p>
 * Version 1.1 - Direct - View/Edit/Activate Studio Contests Assembly Change Note
 * - remove useless imports
 * </p>
 * <p>
 * Version 1.2 - TC Direct - Software Contest Creation Update Assembly Change Note
 * - add method getCurrentProjectCopilots to return copilots of current selected project.
 * </p>
 * <p>
 * Version 1.2.1 - Release Assembly - Direct Improvements Assembly Release 3 Change Note
 * - add method getCurrentServerDate to return current server date.
 * </p>
 * <p>
 * Changes in version 1.2.2 (TC Cockpit Post a Copilot Assembly 2):
 * <ol>
 * <li>Added {@link #getAllProjectCopilotTypes()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Version 1.3 (Module Assembly - TC Cockpit Contest Milestone Association 1)
 * <ul>
 *     <li>Adds method {@link #getCurrentProjectMilestones()} to get the milestones of current selected project if exists</li>
 * </ul>
 * </p>
 *
 * <p>
 * Version 1.4 (BUGR-10221 TopCoder Cockpit CMC Account ID for launching new contest)
 * <ul>
 *     <li>Added {@link #cmcAccountId} and {@link #cmcBillingAccount}</li>
 *     <li>Updated {@link #executeAction()} to get billing account id by cmc account id</li>
 * </ul>
 * </p>
 *
 * @author BeBetter, duxiaoyang, GreatKevin, Veve
 * @version 1.4
 */
public class LaunchContestAction extends ContestAction {
    private CommonDTO viewData =  new CommonDTO();

    private SessionData sessionData;

    /**
    * <p> Whether user is admin</p>
    */
    private boolean admin = false;

    /**
     * The CMC Account ID.
     *
     * @since 1.4
     */
    private String cmcAccountId;

    /**
     * The Billing Account got from CMC Account ID.
     *
     * @since 1.4
     */
    private IdNamePair cmcBillingAccount;

    /**
     * <p>
     * Executes the action. Does nothing for now.
     * </p>
     */
    @Override
    protected void executeAction() throws Exception {
        // it is to conform to existing source codes
        HttpServletRequest request = ServletActionContext.getRequest();

        HttpSession session = request.getSession(false);
        if (session != null) {
            sessionData = new SessionData(session);

            TCSubject user = sessionData.getCurrentUser();
            admin = DirectUtils.isRole(user, "Administrator");
        }

        List<ProjectBriefDTO> projects = DataProvider.getUserProjects(sessionData.getCurrentUserId());

        UserProjectsDTO userProjectsDTO = new UserProjectsDTO();
        userProjectsDTO.setProjects(projects);
        viewData.setUserProjects(userProjectsDTO);

        // get cmc billing account with cmc account id
        if(getCmcAccountId() != null && getCmcAccountId().trim().length() > 0) {
            setCmcBillingAccount(DataProvider.getBillingAccountFromCMCAccountID(getCmcAccountId()));
        }
    }

    public CommonDTO getViewData() {
        return viewData;
    }

    public SessionData getSessionData() {
        return sessionData;
    }

	/**
     * Gets the current server date.
     *
     * @return the current server date.
     * @since 1.2.1
     */
    public Date getCurrentServerDate() {
        return new Date();
    }

    /**
    * <p>Property to determine whether user is admin</p>
    */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Gets the copilots assigned to current selected project.
     *
     * @return the copilots assigned to current selected project.
     * @throws Exception if there is any error.
     * @since  1.2
     */
    public List<ContestCopilotDTO> getCurrentProjectCopilots() throws Exception {
        // get current selected project from session first
        Long currentProjectId = sessionData.getCurrentSelectDirectProjectID();

        if (currentProjectId == null) {
            // check project context again
            if (sessionData.getCurrentProjectContext() != null) {
                currentProjectId = Long.valueOf(sessionData.getCurrentProjectContext().getId());
            }
        }

        List<ContestCopilotDTO> result = new ArrayList();

        if (currentProjectId != null) {
            // get copilots of the project
            result = DataProvider.getCopilotsForDirectProject(currentProjectId.longValue());
        }

        return result;
    }

    /**
     * Gets the milestones of the current selected project.
     *
     * @return a list of milestone instances.
     * @throws Exception if any error.
     * @since 1.3
     */
    public List<Milestone> getCurrentProjectMilestones() throws Exception {
        // get current selected project from session first
        Long currentProjectId = sessionData.getCurrentSelectDirectProjectID();

        if (currentProjectId == null) {
            // check project context again
            if (sessionData.getCurrentProjectContext() != null) {
                currentProjectId = Long.valueOf(sessionData.getCurrentProjectContext().getId());
            }
        }

        List<Milestone> result = new ArrayList();

        if (currentProjectId != null) {
            // get copilots of the project
            result = getMilestoneService().getAll(currentProjectId,
                                                  Arrays.asList(MilestoneStatus.values()),
                                                  SortOrder.ASCENDING);
        }

        return result;
    }

    /**
     * <p>
     * Gets the mapping to be used for looking up the project copilot types by IDs.
     * </p>
     * @return a <code>Map</code> mapping the project copilot type ids to category names.
     * @throws Exception
     *             if an unexpected error occurs.
     * @since 1.2.2
     */
    public Map<Long, String> getAllProjectCopilotTypes() throws Exception {
        return DataProvider.getAllProjectCopilotTypes();
    }

    /**
     * Gets the CMC Account id.
     *
     * @return the CMC Account Id.
     * @since 1.4
     */
    public String getCmcAccountId() {
        return cmcAccountId;
    }

    /**
     * Sets the CMC Account Id.
     *
     * @param cmcAccountId the CMC Account id.
     * @since 1.4
     */
    public void setCmcAccountId(String cmcAccountId) {
        this.cmcAccountId = cmcAccountId;
    }

    /**
     * Gets the CMC Billing Account.
     *
     * @return the CMC Billing Account.
     * @since 1.4
     */
    public IdNamePair getCmcBillingAccount() {
        return cmcBillingAccount;
    }

    /**
     * Sets the CMC Billing Account.
     *
     * @param cmcBillingAccount the CMC Billing account.
     * @since 1.4
     */
    public void setCmcBillingAccount(IdNamePair cmcBillingAccount) {
        this.cmcBillingAccount = cmcBillingAccount;
    }
}

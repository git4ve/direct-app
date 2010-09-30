/*
 * Copyright (C) 2010 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.services.view.ajax;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import com.topcoder.catalog.entity.Category;
import com.topcoder.catalog.entity.CompDocumentation;
import com.topcoder.catalog.entity.Technology;
import com.topcoder.catalog.service.AssetDTO;
import com.topcoder.direct.services.configs.ConfigUtils;
import com.topcoder.direct.services.view.util.DirectUtils;
import com.topcoder.management.project.Project;
import com.topcoder.service.project.SoftwareCompetition;

/**
 * <p>
 * Bean processor for <code>SoftwareCompetition</code>.
 * </p>
 * <p>
 * Version 1.1 - Direct - Repost and New Version v1.0 Assembly Change Note
 * <li>
 * add project status in the response.</li>
 * <ul>
 * </ul>
 * </p>
 *
 * @author BeBetter, TCSDEVELOPER
 * @version 1.1
 * @since Direct - View/Edit/Activate Software Contests Assembly
 */
public class SoftwareCompetitionBeanProcessor implements JsonBeanProcessor {
    /**
     * <p>
     * The constant for design category.
     * </p>
     */
    private static final long CATEGORY_DESIGN = 1;

    /**
     * <p>
     * The constant for dev category.
     * </p>
     */
    private static final long CATEGORY_DEV = 2;

    /**
     * <p>
     * The constant for concept category.
     * </p>
     */
    private static final long CATEGORY_CONCEPT = 23;

    /**
     * <p>
     * The constant for spec category.
     * </p>
     */
    private static final long CATEGORY_SPEC = 6;

    /**
     * <p>
     * Processes the bean.
     * </p>
     *
     * @param bean the bean to be processed.
     * @param jsonConfig the configuration when processing. it will be ignored
     * @return json object for the bean
     * @throws IllegalArgumentException if the bean is not of type <code>StudioCompetition</code>
     */
    public JSONObject processBean(Object bean, JsonConfig jsonConfig) {
        if (!(bean instanceof SoftwareCompetition)) {
            throw new IllegalArgumentException("bean type should be StudioCompetition.");
        }

        return (JSONObject) JSONSerializer.toJSON(getMapResult((SoftwareCompetition) bean), jsonConfig);
    }

    /**
     * <p>
     * Gets the map result for the bean.
     * </p>
     *
     * @param bean the bean
     * @return the map result for the given bean
     */
    private Object getMapResult(SoftwareCompetition bean) {
        Map<String, Object> result = new HashMap<String, Object>();

        Project project = bean.getProjectHeader();
        AssetDTO assetDTO = bean.getAssetDTO();

        result.put("contestId", project.getId());
        result.put("projectStatus", project.getProjectStatus());
        result.put("projectCategory", project.getProjectCategory());
        result.put("contestName", assetDTO.getName());
        result.put("startDate", assetDTO.getProductionDate());
        result.put("tcDirectProjectId", project.getTcDirectProjectId());
        result.put("tcDirectProjectName", project.getTcDirectProjectName());
        result.put("billingProjectId", project.getProperties().get("Billing Project"));
        result.put("adminFee", project.getProperties().get("Admin Fee"));

        // project info properties map
        result.put("properties", project.getProperties());

        // spec
        result.put("detailedRequirements", project.getProjectSpec().getDetailedRequirements());
        result.put("softwareGuidelines", project.getProjectSpec().getFinalSubmissionGuidelines());
        // technologies/categories for development/design
        if (isDevOrDesign(bean)) {
            result.put("rootCategoryId", assetDTO.getRootCategory().getId());
            result.put("categoryIds", CollectionUtils.collect(assetDTO.getCategories(), new Transformer() {
                public Object transform(Object object) {
                    return ((Category) object).getId() + "";
                }
            }));
        }

        if (isTechnologyContest(bean)) {
            result.put("technologyIds", CollectionUtils.collect(assetDTO.getTechnologies(), new Transformer() {
                public Object transform(Object object) {
                    return ((Technology) object).getId() + "";
                }
            }));
        }

        // documentation
        result.put("documentation", CollectionUtils.collect(assetDTO.getDocumentation(), new Transformer() {
            public Object transform(Object object) {
                CompDocumentation doc = (CompDocumentation) object;
                Map<String, Object> docMap = new HashMap<String, Object>();
                docMap.put("documentId", doc.getId());
                docMap.put("fileName", doc.getUrl().substring(doc.getUrl().lastIndexOf("/") + 1));
                docMap.put("description", doc.getDocumentName());
                docMap.put("documentTypeId", doc.getDocumentTypeId());
                return docMap;
            }
        }));

        // populate software contest fee so we could show prizes data on time
        result.put("softwareContestFees", ConfigUtils.getSoftwareContestFees());

        // end date
        result.put("endDate", DirectUtils.getDateString(DirectUtils.getEndDate(bean)));
        result.put("paidFee", DirectUtils.getPaidFee(bean));

        result.put("phaseOpen", DirectUtils.isPhaseOpen(bean));
        result.put("hasSpecReview", DirectUtils.hasSpecReview(bean));
        result.put("isSpecReviewStarted", DirectUtils.isSpecReviewStarted(bean));



        return result;
    }

    /**
     * <p>
     * Determines if it is development or design competition.
     * </p>
     *
     * @param bean the software competition
     * @return true if it is development or design competition
     */
    private boolean isDevOrDesign(SoftwareCompetition bean) {
        long categoryId = bean.getProjectHeader().getProjectCategory().getId();
        return (CATEGORY_DESIGN == categoryId || CATEGORY_DEV == categoryId);
    }

    /**
     * <p>
     * Determines if it needs technology.
     * </p>
     *
     * @param bean the software competition
     * @return true if it needs technology
     */
    private boolean isTechnologyContest(SoftwareCompetition bean) {
        long categoryId = bean.getProjectHeader().getProjectCategory().getId();
        return !(CATEGORY_CONCEPT == categoryId || CATEGORY_SPEC == categoryId);
    }
}

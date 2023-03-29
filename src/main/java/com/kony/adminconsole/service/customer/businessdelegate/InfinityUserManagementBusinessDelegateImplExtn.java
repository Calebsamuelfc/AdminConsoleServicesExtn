package com.kony.adminconsole.service.customer.businessdelegate;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.adminconsole.commons.utils.CommonUtilities;

import java.util.HashMap;
import java.util.Map;

import com.kony.adminconsole.service.customer.businessdelegate.impl.InfinityUserManagementBusinessDelegateImpl;
import com.kony.adminconsole.service.customer.resource.impl.InfinityUserManagementResourceImpl;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class InfinityUserManagementBusinessDelegateImplExtn extends InfinityUserManagementBusinessDelegateImpl {

    private static final Logger LOG = Logger.getLogger(InfinityUserManagementBusinessDelegateImplExtn.class);
    public JSONObject getAssociatedCustomers(Map<String, Object> postParametersMap, String dbpServicesClaimsToken) throws DBPApplicationException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("backendToken", dbpServicesClaimsToken);
        String serviceResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBPServices").withOperationId("getAssociatedCustomers").withRequestHeaders(headerMap).withRequestParameters(postParametersMap).withPassThroughOutput(true).build().getResponse();
        return CommonUtilities.getStringAsJSONObject(serviceResponse);
    }

    public JSONObject getAllEligibleRelationalCustomers(Map<String, Object> postParametersMap, String dbpServicesClaimsToken) throws DBPApplicationException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("backendToken", dbpServicesClaimsToken);
        String serviceResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBPServices").withOperationId("getAllEligibleRelationalCustomers").withRequestHeaders(headerMap).withRequestParameters(postParametersMap).withPassThroughOutput(true).build().getResponse();
        return CommonUtilities.getStringAsJSONObject(serviceResponse);
    }

    public JSONObject createInfinityUser(Map<String, Object> postParametersMap, String dbpServicesClaimsToken) throws DBPApplicationException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("backendToken", dbpServicesClaimsToken);
        String serviceResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBPServices").withOperationId("createInfinityUser").withRequestHeaders(headerMap).withRequestParameters(postParametersMap).withPassThroughOutput(true).build().getResponse();
        return CommonUtilities.getStringAsJSONObject(serviceResponse);
    }

    public JSONObject editInfinityUser(Map<String, Object> postParametersMap, String dbpServicesClaimsToken) throws DBPApplicationException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("backendToken", dbpServicesClaimsToken);
        String serviceResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBPServices").withOperationId("editInfinityUser").withRequestHeaders(headerMap).withRequestParameters(postParametersMap).withPassThroughOutput(true).build().getResponse();

        LOG.error("29032023 serviceResponse: " + serviceResponse);

        return CommonUtilities.getStringAsJSONObject(serviceResponse);
    }

    public JSONObject getInfinityUser(Map<String, Object> postParametersMap, String dbpServicesClaimsToken) throws DBPApplicationException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("backendToken", dbpServicesClaimsToken);
        String serviceResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBPServices").withOperationId("getInfinityUser").withRequestHeaders(headerMap).withRequestParameters(postParametersMap).withPassThroughOutput(true).build().getResponse();
        return CommonUtilities.getStringAsJSONObject(serviceResponse);
    }

    public JSONObject getCoreCustomerRoleFeatureActionLimits(Map<String, Object> postParametersMap, String dbpServicesClaimsToken) throws DBPApplicationException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("backendToken", dbpServicesClaimsToken);
        String serviceResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBPServices").withOperationId("getCoreCustomerRoleFeatureActionLimits").withRequestHeaders(headerMap).withRequestParameters(postParametersMap).withPassThroughOutput(true).build().getResponse();
        return CommonUtilities.getStringAsJSONObject(serviceResponse);
    }

    public JSONObject getRelativeCoreCustomerContractDetails(Map<String, Object> postParametersMap, String dbpServicesClaimsToken) throws DBPApplicationException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("backendToken", dbpServicesClaimsToken);
        String serviceResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBPServices").withOperationId("getRelativeCoreCustomerContractDetails").withRequestHeaders(headerMap).withRequestParameters(postParametersMap).withPassThroughOutput(true).build().getResponse();
        return CommonUtilities.getStringAsJSONObject(serviceResponse);
    }

    public JSONObject getCoreCustomerContractDetails(Map<String, Object> postParametersMap, String dbpServicesClaimsToken) throws DBPApplicationException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("backendToken", dbpServicesClaimsToken);
        String serviceResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBPServices").withOperationId("getCoreCustomerContractDetails").withRequestHeaders(headerMap).withRequestParameters(postParametersMap).withPassThroughOutput(true).build().getResponse();
        return CommonUtilities.getStringAsJSONObject(serviceResponse);
    }

    public JSONObject getInfinityUserContractDetails(Map<String, Object> postParametersMap, String dbpServicesClaimsToken) throws DBPApplicationException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("backendToken", dbpServicesClaimsToken);
        String serviceResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBPServices").withOperationId("getInfinityUserContractDetails").withRequestHeaders(headerMap).withRequestParameters(postParametersMap).withPassThroughOutput(true).build().getResponse();
        return CommonUtilities.getStringAsJSONObject(serviceResponse);
    }

    public JSONObject getInfinityUserAccounts(Map<String, Object> postParametersMap, String dbpServicesClaimsToken) throws DBPApplicationException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("backendToken", dbpServicesClaimsToken);
        String serviceResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBPServices").withOperationId("getInfinityUserAccounts").withRequestHeaders(headerMap).withRequestParameters(postParametersMap).withPassThroughOutput(true).build().getResponse();
        return CommonUtilities.getStringAsJSONObject(serviceResponse);
    }

    public JSONObject getInfinityUserFeatureActions(Map<String, Object> postParametersMap, String dbpServicesClaimsToken) throws DBPApplicationException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("backendToken", dbpServicesClaimsToken);
        String serviceResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBPServices").withOperationId("getInfinityUserFeatureActions").withRequestHeaders(headerMap).withRequestParameters(postParametersMap).withPassThroughOutput(true).build().getResponse();
        return CommonUtilities.getStringAsJSONObject(serviceResponse);
    }

    public JSONObject getInfinityUserLimits(Map<String, Object> postParametersMap, String dbpServicesClaimsToken) throws DBPApplicationException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("backendToken", dbpServicesClaimsToken);
        String serviceResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBPServices").withOperationId("getInfinityUserLimits").withRequestHeaders(headerMap).withRequestParameters(postParametersMap).withPassThroughOutput(true).build().getResponse();
        return CommonUtilities.getStringAsJSONObject(serviceResponse);
    }
}

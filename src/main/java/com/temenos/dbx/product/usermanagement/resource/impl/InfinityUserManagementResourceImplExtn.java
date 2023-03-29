package com.temenos.dbx.product.usermanagement.resource.impl;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.sessionmanager.SessionScope;
import com.kony.dbputilities.util.BundleConfigurationHandler;
import com.kony.dbputilities.util.ConvertJsonToResult;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.IntegrationTemplateURLFinder;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.temenos.dbx.mfa.dto.MFAServiceDTO;
import com.temenos.dbx.mfa.utils.MFAServiceUtil;
import com.temenos.dbx.product.approvalmatrixservices.businessdelegate.api.ApprovalMatrixBusinessDelegate;
import com.temenos.dbx.product.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.dbx.product.businessdelegate.api.FeatureBusinessDelegate;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.contract.backenddelegate.api.ContractBackendDelegate;
import com.temenos.dbx.product.contract.backenddelegate.api.ContractCoreCustomerBackendDelegate;
import com.temenos.dbx.product.contract.backenddelegate.api.CoreCustomerBackendDelegate;
import com.temenos.dbx.product.contract.backenddelegate.api.ServiceDefinitionBackendDelegate;
import com.temenos.dbx.product.contract.businessdelegate.api.ContractBusinessDelegate;
import com.temenos.dbx.product.contract.businessdelegate.api.ContractFeatureActionsBusinessDelegate;
import com.temenos.dbx.product.contract.businessdelegate.api.CoreCustomerBusinessDelegate;
import com.temenos.dbx.product.contract.resource.api.ContractResource;
import com.temenos.dbx.product.dto.AllAccountsViewDTO;
import com.temenos.dbx.product.dto.ApplicationDTO;
import com.temenos.dbx.product.dto.BackendIdentifierDTO;
import com.temenos.dbx.product.dto.ContractAccountsDTO;
import com.temenos.dbx.product.dto.ContractCustomersDTO;
import com.temenos.dbx.product.dto.CustomerAccountsDTO;
import com.temenos.dbx.product.dto.CustomerCommunicationDTO;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.CustomerGroupDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.FeatureActionLimitsDTO;
import com.temenos.dbx.product.dto.MembershipDTO;
import com.temenos.dbx.product.dto.ServiceDefinitionDTO;
import com.temenos.dbx.product.dto.UserCustomerViewDTO;
import com.temenos.dbx.product.signatorygroupservices.resource.api.SignatoryGroupResource;
import com.temenos.dbx.product.usermanagement.backenddelegate.api.BackendIdentifiersBackendDelegate;
import com.temenos.dbx.product.usermanagement.backenddelegate.api.CommunicationBackendDelegate;
import com.temenos.dbx.product.usermanagement.backenddelegate.impl.InfinityUserManagementBackendDelegateImpl;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CommunicationBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CustomerAccountsBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CustomerActionsBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CustomerGroupBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.InfinityUserManagementBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.PushExternalEventBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.UserManagementBusinessDelegate;
import com.temenos.dbx.product.usermanagement.resource.CoreCustomerResource;
import com.temenos.dbx.product.usermanagement.resource.api.InfinityUserManagementResource;
import com.temenos.dbx.product.usermanagement.resource.api.PushExternalEventResource;
import com.temenos.dbx.product.utils.CustomerCreationMode;
import com.temenos.dbx.product.utils.DTOUtils;
import com.temenos.dbx.product.utils.InfinityConstants;
import com.temenos.dbx.product.utils.ThreadExecutor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class InfinityUserManagementResourceImplExtn extends InfinityUserManagementResourceImpl {
    LoggerUtil logger = new LoggerUtil(InfinityUserManagementResourceImplExtn.class);

    SimpleDateFormat idFormatter = new SimpleDateFormat("yyMMddHHmmssSSS");


    public Object editInfinityUser(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) {
        Result result = new Result();
        JsonObject jsonObject = new JsonObject();
        Map<String, String> map = HelperMethods.getInputParamMap(inputArray);
        Iterator<String> iterator = request.getParameterNames();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if ((!map.containsKey(key) || StringUtils.isBlank(map.get(key))) &&
                    StringUtils.isNotBlank(request.getParameter(key)))
                map.put(key, request.getParameter(key));
        }
        if (!map.containsKey("userDetails") ||
                StringUtils.isBlank(map.get("userDetails"))) {
            ErrorCodeEnum.ERR_10056.setErrorCode(result);
            return Boolean.valueOf(false);
        }
        String userDetails = map.get("userDetails");
        JsonElement userDetailsElement = (new JsonParser()).parse(userDetails);
        if (userDetailsElement.isJsonNull() || !userDetailsElement.isJsonObject()) {
            ErrorCodeEnum.ERR_10056.setErrorCode(result);
            return Boolean.valueOf(false);
        }
        Boolean isContractValidationRequired = Boolean.valueOf(false);
        JsonObject userDetailsJsonObject = userDetailsElement.getAsJsonObject();
        jsonObject.add("userDetails", (JsonElement)userDetailsJsonObject);
        String id = (userDetailsJsonObject.has("id") && !userDetailsJsonObject.get("id").isJsonNull()) ? userDetailsJsonObject.get("id").getAsString() : null;
        if (validateinput(jsonObject, result, map, request, isContractValidationRequired, id)) {
            InfinityUserManagementBusinessDelegate infinityUserManagementBusinessDelegate = (InfinityUserManagementBusinessDelegate)DBPAPIAbstractFactoryImpl.getBusinessDelegate(InfinityUserManagementBusinessDelegate.class);
            DBXResult dbxResult = infinityUserManagementBusinessDelegate.editInfinityUser(jsonObject, request.getHeaderMap());
            if (dbxResult.getResponse() != null) {
                JsonObject jsonResultObject = (JsonObject)dbxResult.getResponse();
                result = JSONToResult.convert(jsonResultObject.toString());
                try {
                    String signatoryGroups = request.getParameter("signatoryGroups");
                    jsonObject.addProperty("signatoryGroups", signatoryGroups);
                } catch (Exception exception) {}
                updateSignatoryGroupEntry(id, jsonObject, result, request);
                this.logger.debug("Json response " + ResultToJSON.convert(result).toString());
            }
        }
        return result;
    }

    private boolean validateinput(JsonObject jsonObject, Result result, Map<String, String> map, DataControllerRequest dcRequest, Boolean isContractValidationRequired, String id) {
        JsonElement jsonElement1 = null;
        JsonElement jsonElement2 = null;
        Map<String, Set<String>> customerAccountsMap = new HashMap<>();
        String customerId = null;
        if (isContractValidationRequired.booleanValue())
            customerId = HelperMethods.getCustomerIdFromSession(dcRequest);
        String removedCompanies = map.get("removedCompanies");
        if (StringUtils.isNotBlank(removedCompanies)) {
            JsonElement removedCompaniesElement = (new JsonParser()).parse(removedCompanies);
            if (!removedCompaniesElement.isJsonNull() && removedCompaniesElement.isJsonArray())
                jsonObject.add("removedCompanies", (JsonElement)removedCompaniesElement.getAsJsonArray());
        }
        if (!map.containsKey("companyList") ||
                StringUtils.isBlank(map.get("companyList")))
            return true;
        String companyList = map.get("companyList");
        JsonElement companyListElement = (new JsonParser()).parse(companyList);
        if (companyListElement.isJsonNull() || !companyListElement.isJsonArray()) {
            ErrorCodeEnum.ERR_10050.setErrorCode(result, "Invalid CompanyList");
            return false;
        }
        Map<String, Set<String>> customerContracts = new HashMap<>();
        Map<String, Set<String>> contractCIFs = new HashMap<>();
        Map<String, Map<String, Set<String>>> customerAccounts = new HashMap<>();
        Map<String, Map<String, Set<String>>> contractAccounts = new HashMap<>();
        Map<String, Set<String>> loggedInUserPermisions = new HashMap<>();
        Map<String, Map<String, Map<String, Map<String, Double>>>> loggedInUserLimits = new HashMap<>();
        if (isContractValidationRequired.booleanValue()) {
            getLoggedInUserContracts(customerId, customerContracts, dcRequest.getHeaderMap());
            getAccountsForCustomer(customerId, customerAccounts, dcRequest.getHeaderMap());
            getLoggedInUserPermissions(customerId, loggedInUserPermisions, loggedInUserLimits, dcRequest);
        }
        Map<String, String> serviceDefinitions = new HashMap<>();
        ContractBackendDelegate backendDelegate = (ContractBackendDelegate)DBPAPIAbstractFactoryImpl.getBackendDelegate(ContractBackendDelegate.class);
        Map<String, FeatureActionLimitsDTO> featureActionsLimitsDTOs = new HashMap<>();
        JsonArray excludedCompaniesArray = new JsonArray();
        JsonArray companiesArray = companyListElement.getAsJsonArray();
        for (int i = 0; i < companiesArray.size(); i++) {
            JsonObject companyJsonObject = companiesArray.get(i).getAsJsonObject();
            String contractId = null;
            String cif = null;
            String serviceDefinition = null;
            String userRole = null;
            boolean bool;
            boolean autoSyncAccounts = false;
            if (companyJsonObject.has("contractId") &&
                    !companyJsonObject.get("contractId").isJsonNull())
                contractId = companyJsonObject.get("contractId").getAsString();
            if (companyJsonObject.has("cif") &&
                    !companyJsonObject.get("cif").isJsonNull())
                cif = companyJsonObject.get("cif").getAsString();
            if (companyJsonObject.has("isPrimary") &&
                    !companyJsonObject.get("isPrimary").isJsonNull())
                bool = Boolean.parseBoolean(companyJsonObject.get("isPrimary").getAsString());
            if (companyJsonObject.has("serviceDefinition") &&
                    !companyJsonObject.get("serviceDefinition").isJsonNull())
                serviceDefinition = companyJsonObject.get("serviceDefinition").getAsString();
            serviceDefinitions.put(contractId, serviceDefinition);
            if (companyJsonObject.has("roleId") &&
                    !companyJsonObject.get("roleId").isJsonNull())
                userRole = companyJsonObject.get("roleId").getAsString();
            if (companyJsonObject.has("autoSyncAccounts") &&
                    !companyJsonObject.get("autoSyncAccounts").isJsonNull())
                autoSyncAccounts = Boolean.parseBoolean(companyJsonObject
                        .get("autoSyncAccounts").getAsString());
            companyJsonObject.addProperty("autoSyncAccounts", autoSyncAccounts + "");
            if (HelperMethods.isBlank(new String[] { contractId, cif, serviceDefinition, userRole })) {
                ErrorCodeEnum.ERR_10050.setErrorCode(result, "Invalid company details");
                return false;
            }
            if (isContractValidationRequired.booleanValue())
                if (isContractValidationRequired.booleanValue() && (!customerContracts.containsKey(contractId) ||
                        !((Set)customerContracts.get(contractId)).contains(cif))) {
                    companiesArray.remove(i);
                    i--;
                    continue;
                }
            getContractCIFs(contractId, contractCIFs, dcRequest.getHeaderMap());
            if (!contractCIFs.containsKey(contractId) || !((Set)contractCIFs.get(contractId)).contains(cif)) {
                companiesArray.remove(i);
                i--;
            } else {
                JsonElement accountsEelement = companyJsonObject.get("accounts");
                if (accountsEelement.isJsonNull() || !accountsEelement.isJsonArray()) {
                    ErrorCodeEnum.ERR_10050.setErrorCode(result, "Invalid accounts");
                    return false;
                }
                getAccountsForContract(contractId, contractAccounts, dcRequest.getHeaderMap());
                JsonArray accountsArray = accountsEelement.getAsJsonArray();
                Set<String> accountList = new HashSet<>();
                Boolean isUserLevelAccountAccessDelegationEnabled = Boolean.valueOf(true);
                try {
                    FeatureActionLimitsDTO coreCustomerFeatureActionDTO = backendDelegate.getRestrictiveFeatureActionLimits(serviceDefinition, contractId, userRole, cif, "", dcRequest
                            .getHeaderMap(), true, "");
                    featureActionsLimitsDTOs.put(cif, coreCustomerFeatureActionDTO);
                } catch (ApplicationException e) {
                    e.printStackTrace();
                }
                JsonArray excludedAccountsArray = (companyJsonObject.has("excludedAccounts") && companyJsonObject.get("excludedAccounts").isJsonArray()) ? companyJsonObject.get("excludedAccounts").getAsJsonArray() : new JsonArray();
                for (int j = 0; j < accountsArray.size(); j++) {
                    JsonObject accountJsonObject = accountsArray.get(j).getAsJsonObject();
                    String accountID = null;
                    boolean isEnabled = true;
                    if (accountJsonObject.has("accountId") &&
                            !accountJsonObject.get("accountId").isJsonNull())
                        accountID = accountJsonObject.get("accountId").getAsString();
                    if (accountJsonObject.has("isEnabled") &&
                            !accountJsonObject.get("isEnabled").isJsonNull())
                        isEnabled = Boolean.parseBoolean(accountJsonObject.get("isEnabled").getAsString());
                    if (!contractAccounts.containsKey(contractId) || !((Map)contractAccounts.get(contractId)).containsKey(cif) ||
                            !((Set)((Map)contractAccounts.get(contractId)).get(cif)).contains(accountID)) {
                        accountsArray.remove(j);
                        j--;
                    } else if (isContractValidationRequired.booleanValue() && isUserLevelAccountAccessDelegationEnabled.booleanValue() && (
                            !customerAccounts.containsKey(contractId) ||
                                    !((Map)customerAccounts.get(contractId)).containsKey(cif) ||
                                    !((Set)((Map)customerAccounts.get(contractId)).get(cif)).contains(accountID))) {
                        accountsArray.remove(j);
                        j--;
                    } else if (isEnabled) {
                        accountList.add(accountID);
                    } else {
                        excludedAccountsArray.add(accountsArray.get(j));
                        accountsArray.remove(j);
                        j--;
                    }
                }
                if (accountList.size() <= 0) {
                    ErrorCodeEnum.ERR_10050.setErrorCode(result, "At least one account should be present");
                    return false;
                }
                customerAccountsMap.put(cif, accountList);
                companyJsonObject.add("excludedAccounts", (JsonElement)excludedAccountsArray);
            }
            continue;
        }
        jsonObject.add("companyList", (JsonElement)companiesArray);
        boolean isAccountLevelAllowed = true;
        if (!isAccountLevelAllowed) {
            map.remove("accountLevelPermissions");
        } else {
            String accountLevelPermissions = map.get("accountLevelPermissions");
            JsonObject jsonObject3 = new JsonObject();
            try {
                jsonElement1 = (new JsonParser()).parse(accountLevelPermissions);
            } catch (Exception exception) {}
            if (!jsonElement1.isJsonNull() && jsonElement1.isJsonArray()) {
                JsonArray accountLevelPermissionsArray = jsonElement1.getAsJsonArray();
                for (int j = 0; j < accountLevelPermissionsArray.size(); j++) {
                    JsonObject accountLevelPermissionsJsonObject = accountLevelPermissionsArray.get(j).getAsJsonObject();
                    String cif = accountLevelPermissionsJsonObject.get("cif").getAsString();
                    JsonElement accountsElement = accountLevelPermissionsJsonObject.get("accounts");
                    FeatureActionLimitsDTO featureActionLimitsDTO = featureActionsLimitsDTOs.get(cif);
                    if (!featureActionsLimitsDTOs.containsKey(cif) || (isContractValidationRequired
                            .booleanValue() && !loggedInUserPermisions.containsKey(cif))) {
                        accountLevelPermissionsArray.remove(j);
                        j--;
                    } else {
                        Map<String, Set<String>> accoutLevelActions = featureActionLimitsDTO.getFeatureaction();
                        Map<String, Map<String, Map<String, String>>> monitoryActions = featureActionLimitsDTO.getMonetaryActionLimits();
                        if (!accountsElement.isJsonNull() && accountsElement.isJsonArray()) {
                            JsonArray accounts = accountsElement.getAsJsonArray();
                            for (int k = 0; k < accounts.size(); k++) {
                                JsonObject accountJsonObject = accounts.get(k).getAsJsonObject();
                                String accountId = accountJsonObject.get("accountId").getAsString();
                                if (!customerAccountsMap.containsKey(cif) ||
                                        !((Set)customerAccountsMap.get(cif)).contains(accountId)) {
                                    accounts.remove(k);
                                    k--;
                                } else {
                                    JsonArray featurePermissions = accountJsonObject.get("featurePermissions").getAsJsonArray();
                                    for (int m = 0; m < featurePermissions.size(); m++) {
                                        JsonObject featurePermissionsJsonObject = featurePermissions.get(m).getAsJsonObject();
                                        String featureId = featurePermissionsJsonObject.get("featureId").getAsString();
                                        if (!accoutLevelActions.containsKey(featureId) &&
                                                !monitoryActions.containsKey(featureId)) {
                                            featurePermissions.remove(m);
                                            m--;
                                        } else {
                                            JsonArray permissions = featurePermissionsJsonObject.get("permissions").getAsJsonArray();
                                            for (int l = 0; l < permissions.size(); l++) {
                                                JsonObject permissonsJsonObject = permissions.get(l).getAsJsonObject();
                                                String actionId = null;
                                                if (permissonsJsonObject.has("id"))
                                                    actionId = permissonsJsonObject.get("id").getAsString();
                                                if (StringUtils.isBlank(actionId) && permissonsJsonObject
                                                        .has("actionId"))
                                                    actionId = permissonsJsonObject.get("actionId").getAsString();
                                                Boolean isEnabled = Boolean.valueOf(Boolean.parseBoolean(permissonsJsonObject
                                                        .get("isEnabled").getAsString()));
                                                Boolean isAccountLevel = Boolean.valueOf("1".equals(
                                                        JSONUtil.getString((JsonObject)featureActionLimitsDTO.getActionsInfo().get(actionId), "isAccountLevel")));
                                                if (((accoutLevelActions.get(featureId) == null ||
                                                        !((Set)accoutLevelActions.get(featureId)).contains(actionId)) && (monitoryActions
                                                        .get(featureId) == null ||
                                                        !((Map)monitoryActions.get(featureId)).containsKey(actionId)) &&
                                                        !isAccountLevel.booleanValue()) || !isEnabled.booleanValue() || (isContractValidationRequired
                                                        .booleanValue() &&
                                                        !((Set)loggedInUserPermisions.get(cif)).contains(actionId))) {
                                                    permissions.remove(l);
                                                    l--;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                jsonObject.add("accountLevelPermissions", (JsonElement)accountLevelPermissionsArray);
            }
        }
        String globalLevelPermissions = map.get("globalLevelPermissions");
        JsonObject jsonObject1 = new JsonObject();
        try {
            jsonElement1 = (new JsonParser()).parse(globalLevelPermissions);
        } catch (Exception exception) {}
        if (!jsonElement1.isJsonNull() && jsonElement1.isJsonArray()) {
            JsonArray globalLevelPermissionsArray = jsonElement1.getAsJsonArray();
            for (int j = 0; j < globalLevelPermissionsArray.size(); j++) {
                JsonObject globalLevelPermissionJsonObject = globalLevelPermissionsArray.get(j).getAsJsonObject();
                String cif = globalLevelPermissionJsonObject.get("cif").getAsString();
                JsonElement featuresElement = globalLevelPermissionJsonObject.get("features");
                FeatureActionLimitsDTO featureActionLimitsDTO = featureActionsLimitsDTOs.get(cif);
                if (!featureActionsLimitsDTOs.containsKey(cif) || (isContractValidationRequired
                        .booleanValue() && !loggedInUserPermisions.containsKey(cif))) {
                    globalLevelPermissionsArray.remove(j);
                    j--;
                } else {
                    Map<String, Set<String>> globalLevelActions = featureActionLimitsDTO.getFeatureaction();
                    if (!featuresElement.isJsonNull() && featuresElement.isJsonArray()) {
                        JsonArray features = featuresElement.getAsJsonArray();
                        for (int k = 0; k < features.size(); k++) {
                            JsonObject featureJsonObject = features.get(k).getAsJsonObject();
                            String featureId = featureJsonObject.get("featureId").getAsString();
                            if (!globalLevelActions.containsKey(featureId)) {
                                features.remove(k);
                                k--;
                            } else {
                                JsonArray permissions = featureJsonObject.get("permissions").getAsJsonArray();
                                for (int m = 0; m < permissions.size(); m++) {
                                    JsonObject permissionJsonObject = permissions.get(m).getAsJsonObject();
                                    String permissionId = "";
                                    if (permissionJsonObject.has("id"))
                                        permissionId = permissionJsonObject.get("id").getAsString();
                                    if (StringUtils.isBlank(permissionId) && permissionJsonObject
                                            .has("actionId"))
                                        permissionId = permissionJsonObject.get("actionId").getAsString();
                                    if (StringUtils.isBlank(permissionId) && permissionJsonObject
                                            .has("permissionType"))
                                        permissionId = permissionJsonObject.get("permissionType").getAsString();
                                    Boolean isEnabled = Boolean.valueOf(Boolean.parseBoolean(permissionJsonObject
                                            .get("isEnabled").getAsString()));
                                    Set<String> permissionsSet = globalLevelActions.get(featureId);
                                    if (!permissionsSet.contains(permissionId) || !isEnabled.booleanValue() || (isContractValidationRequired.booleanValue() &&
                                            !((Set)loggedInUserPermisions.get(cif)).contains(permissionId))) {
                                        permissions.remove(m);
                                        m--;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            jsonObject.add("globalLevelPermissions", (JsonElement)globalLevelPermissionsArray);
        }
        String transactionLimits = map.get("transactionLimits");
        JsonObject jsonObject2 = new JsonObject();
        try {
            jsonElement2 = (new JsonParser()).parse(transactionLimits);
        } catch (Exception exception) {}
        if (!jsonElement2.isJsonNull() && jsonElement2.isJsonArray()) {
            JsonArray transactionLimitsArray = jsonElement2.getAsJsonArray();
            for (int j = 0; j < transactionLimitsArray.size(); j++) {
                JsonObject transactionLimitsJsonObject = transactionLimitsArray.get(j).getAsJsonObject();
                String cif = transactionLimitsJsonObject.get("cif").getAsString();
                FeatureActionLimitsDTO featureActionLimitsDTO = featureActionsLimitsDTOs.get(cif);
                if (!featureActionsLimitsDTOs.containsKey(cif) || (isContractValidationRequired
                        .booleanValue() && !loggedInUserLimits.containsKey(cif))) {
                    transactionLimitsArray.remove(j);
                    j--;
                } else {
                    Map<String, Map<String, Map<String, String>>> transactionLimitsMap = featureActionLimitsDTO.getMonetaryActionLimits();
                    JsonElement accountsElement = transactionLimitsJsonObject.get("accounts");
                    if (!accountsElement.isJsonNull() && accountsElement.isJsonArray()) {
                        JsonArray accounts = accountsElement.getAsJsonArray();
                        for (int k = 0; k < accounts.size(); k++) {
                            JsonObject accountJsonObject = accounts.get(k).getAsJsonObject();
                            String accountId = accountJsonObject.get("accountId").getAsString();
                            if (!customerAccountsMap.containsKey(cif) ||
                                    !((Set)customerAccountsMap.get(cif)).contains(accountId) || (isContractValidationRequired.booleanValue() &&
                                    !((Map)loggedInUserLimits.get(cif)).containsKey(accountId))) {
                                accounts.remove(k);
                                k--;
                            } else {
                                JsonArray featurePermissions = accountJsonObject.get("featurePermissions").getAsJsonArray();
                                for (int m = 0; m < featurePermissions.size(); m++) {
                                    JsonObject featurePermissionJsonObject = featurePermissions.get(m).getAsJsonObject();
                                    String feaureId = featurePermissionJsonObject.get("featureId").getAsString();
                                    String actionId = featurePermissionJsonObject.get("actionId").getAsString();
                                    if (!transactionLimitsMap.containsKey(feaureId) ||
                                            !((Map)transactionLimitsMap.get(feaureId)).containsKey(actionId) || (isContractValidationRequired
                                            .booleanValue() &&
                                            !((Map)((Map)loggedInUserLimits.get(cif)).get(accountId)).containsKey(actionId))) {
                                        featurePermissions.remove(m);
                                        m--;
                                    } else {
                                        featurePermissionJsonObject.add("limitGroupId", ((JsonObject)featureActionLimitsDTO
                                                .getActionsInfo().get(actionId)).get("limitGroupId"));
                                        Map<String, String> limitMap = (Map<String, String>)((Map)transactionLimitsMap.get(feaureId)).get(actionId);
                                        JsonArray limits = featurePermissionJsonObject.get("limits").getAsJsonArray();
                                        for (int l = 0; l < limits.size(); l++) {
                                            JsonObject limit = limits.get(l).getAsJsonObject();
                                            Double limit1 = new Double(0.0D);
                                            Double limit2 = new Double(0.0D);
                                            limit1 = Double.valueOf(Double.parseDouble(limit.get("value").getAsString()));
                                            String limitId = limit.get("id").getAsString();
                                            Double limit3 = null;
                                            if (isContractValidationRequired.booleanValue())
                                                limit3 = (Double)((Map)((Map)((Map)loggedInUserLimits.get(cif)).get(accountId)).get(actionId)).get(limitId);
                                            if (limitId.equals("PRE_APPROVED_DAILY_LIMIT") || limitId
                                                    .equals("AUTO_DENIED_DAILY_LIMIT") || limitId
                                                    .equals("DAILY_LIMIT")) {
                                                limit2 = Double.valueOf(Double.parseDouble((limitMap.containsKey("DAILY_LIMIT") &&
                                                        StringUtils.isNotBlank(limitMap.get("DAILY_LIMIT"))) ? limitMap
                                                        .get("DAILY_LIMIT") : "0.0"));
                                            } else if (limitId.equals("PRE_APPROVED_WEEKLY_LIMIT") || limitId
                                                    .equals("AUTO_DENIED_WEEKLY_LIMIT") || limitId
                                                    .equals("WEEKLY_LIMIT")) {
                                                limit2 = Double.valueOf(Double.parseDouble((limitMap.containsKey("WEEKLY_LIMIT") &&

                                                        StringUtils.isNotBlank(limitMap.get("WEEKLY_LIMIT"))) ? limitMap
                                                        .get("WEEKLY_LIMIT") : "0.0"));
                                            } else if (limitId.equals("PRE_APPROVED_TRANSACTION_LIMIT") || limitId
                                                    .equals("AUTO_DENIED_TRANSACTION_LIMIT") || limitId
                                                    .equals("MAX_TRANSACTION_LIMIT")) {
                                                limit2 = Double.valueOf(Double.parseDouble((limitMap
                                                        .containsKey("MAX_TRANSACTION_LIMIT") &&
                                                        StringUtils.isNotBlank(limitMap
                                                                .get("MAX_TRANSACTION_LIMIT"))) ? limitMap
                                                        .get("MAX_TRANSACTION_LIMIT") : "0.0"));
                                            }
                                            if (limit1.doubleValue() < limit2.doubleValue()) {
                                                limit.addProperty("value", (limit3 != null) ? (
                                                        Math.min(limit1.doubleValue(), limit3.doubleValue()) + "") : limit1.toString());
                                            } else {
                                                limit.addProperty("value", (limit3 != null) ? (
                                                        Math.min(limit2.doubleValue(), limit3.doubleValue()) + "") : limit2.toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            jsonObject.add("transactionLimits", (JsonElement)transactionLimitsArray);
        }
        return true;
    }

    private void getLoggedInUserContracts(String customerId, Map<String, Set<String>> customerContracts, Map<String, Object> headerMap) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(customerId)) {
            String filter = "customerId eq " + customerId;
            map.put("$filter", filter);
            JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(map, headerMap, "contractcustomers.getRecord");
            if (jsonObject.has("contractcustomers")) {
                JsonElement jsonElement = jsonObject.get("contractcustomers");
                if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject customerContract = jsonArray.get(i).getAsJsonObject();
                        String contractId = (customerContract.has("contractId") && !customerContract.get("contractId").isJsonNull()) ? customerContract.get("contractId").getAsString() : null;
                        String coreCustomerId = (customerContract.has("coreCustomerId") && !customerContract.get("coreCustomerId").isJsonNull()) ? customerContract.get("coreCustomerId").getAsString() : null;
                        if (!customerContracts.containsKey(contractId))
                            customerContracts.put(contractId, new HashSet<>());
                        if (customerContracts.containsKey(contractId) &&
                                !((Set)customerContracts.get(contractId)).contains(coreCustomerId))
                            ((Set<String>)customerContracts.get(contractId)).add(coreCustomerId);
                    }
                }
            }
        }
    }

    private void getLoggedInUserPermissions(String customerId, Map<String, Set<String>> loggedInUserPermisions, Map<String, Map<String, Map<String, Map<String, Double>>>> loggedInUserLimits, DataControllerRequest dcRequest) {
        String filter = "Customer_id eq " + customerId;
        Map<String, Object> input = new HashMap<>();
        input.put("$filter", filter);
        JsonObject response = ServiceCallHelper.invokeServiceAndGetJson(input, dcRequest.getHeaderMap(), "customeractionlimits.getRecord");
        if (response.has("customeraction")) {
            JsonElement jsonElement = response.get("customeraction");
            if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                JsonArray actionsArray = jsonElement.getAsJsonArray();
                for (JsonElement element : actionsArray) {
                    String coreCustomerId = element.getAsJsonObject().get("coreCustomerId").getAsString();
                    if (!loggedInUserPermisions.containsKey(coreCustomerId)) {
                        loggedInUserPermisions.put(coreCustomerId, new HashSet<>());
                        loggedInUserLimits.put(coreCustomerId, new HashMap<>());
                    }
                    String account = (element.getAsJsonObject().has("Account_id") && !element.getAsJsonObject().get("Account_id").isJsonNull()) ? element.getAsJsonObject().get("Account_id").getAsString() : "";
                    if (StringUtils.isNotBlank(account) &&
                            !((Map)loggedInUserLimits.get(coreCustomerId)).containsKey(account))
                        ((Map)loggedInUserLimits.get(coreCustomerId)).put(account, new HashMap<>());
                    String actionId = (element.getAsJsonObject().has("Action_id") && !element.getAsJsonObject().get("Action_id").isJsonNull()) ? element.getAsJsonObject().get("Action_id").getAsString() : "";
                    ((Set<String>)loggedInUserPermisions.get(coreCustomerId)).add(actionId);
                    if (StringUtils.isNotBlank(account) &&
                            !((Map)((Map)loggedInUserLimits.get(coreCustomerId)).get(account)).containsKey(actionId))
                        ((Map)((Map)loggedInUserLimits.get(coreCustomerId)).get(account)).put(actionId, new HashMap<>());
                    String limitType = (element.getAsJsonObject().has("LimitType_id") && !element.getAsJsonObject().get("LimitType_id").isJsonNull()) ? element.getAsJsonObject().get("LimitType_id").getAsString() : "";
                    Double value = null;
                    try {
                        value = Double.valueOf((element.getAsJsonObject().has("value") &&
                                !element.getAsJsonObject().get("value").isJsonNull()) ?
                                Double.parseDouble(element
                                        .getAsJsonObject().get("value").getAsString()) : 0.0D);
                    } catch (Exception exception) {}
                    if (StringUtils.isNotBlank(limitType) &&
                            !((Map)((Map)((Map)loggedInUserLimits.get(coreCustomerId)).get(account)).get(actionId)).containsKey(limitType))
                        ((Map<String, Double>)((Map)((Map)loggedInUserLimits.get(coreCustomerId)).get(account)).get(actionId)).put(limitType, value);
                }
            }
        }
    }

    private void getContractCIFs(String contractId, Map<String, Set<String>> contractCIFs, Map<String, Object> headerMap) {
        if (!contractCIFs.containsKey(contractId)) {
            String filter = "contractId eq " + contractId;
            Map<String, Object> map = new HashMap<>();
            map.put("$filter", filter);
            JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(map, headerMap, "contractcorecustomer.readRecord");
            if (jsonObject.has("contractcorecustomers")) {
                JsonElement jsonElement = jsonObject.get("contractcorecustomers");
                if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject contractCoreCustomer = jsonArray.get(i).getAsJsonObject();
                        contractId = (contractCoreCustomer.has("contractId") && !contractCoreCustomer.get("contractId").isJsonNull()) ? contractCoreCustomer.get("contractId").getAsString() : null;
                        String coreCustomerId = (contractCoreCustomer.has("coreCustomerId") && !contractCoreCustomer.get("coreCustomerId").isJsonNull()) ? contractCoreCustomer.get("coreCustomerId").getAsString() : null;
                        if (!contractCIFs.containsKey(contractId))
                            contractCIFs.put(contractId, new HashSet<>());
                        ((Set<String>)contractCIFs.get(contractId)).add(coreCustomerId);
                    }
                }
            }
        }
    }

    private void getAccountsForContract(String contractId, Map<String, Map<String, Set<String>>> contractAccounts, Map<String, Object> headerMap) {
        if (!contractAccounts.containsKey(contractId)) {
            Map<String, Object> map = new HashMap<>();
            String filter = "contractId eq " + contractId;
            map.put("$filter", filter);
            JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(map, headerMap, "contractaccount.readRecord");
            if (jsonObject.has("contractaccounts")) {
                JsonElement jsonElement = jsonObject.get("contractaccounts");
                if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject account = jsonArray.get(i).getAsJsonObject();
                        String coreCustomerId = (account.has("coreCustomerId") && !account.get("coreCustomerId").isJsonNull()) ? account.get("coreCustomerId").getAsString() : null;
                        if (!contractAccounts.containsKey(contractId))
                            contractAccounts.put(contractId, new HashMap<>());
                        if (!((Map)contractAccounts.get(contractId)).containsKey(coreCustomerId))
                            ((Map)contractAccounts.get(contractId)).put(coreCustomerId, new HashSet());
                        ((Set<String>)((Map)contractAccounts.get(contractId)).get(coreCustomerId))
                                .add(account.get("accountId").getAsString());
                    }
                }
            }
        }
    }

    private void getAccountsForCustomer(String customerId, Map<String, Map<String, Set<String>>> customerAccounts, Map<String, Object> headerMap) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(customerId)) {
            String filter = "Customer_id eq " + customerId;
            map.put("$filter", filter);
            JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(map, headerMap, "customeraccounts.getRecord");
            if (jsonObject.has("customeraccounts")) {
                JsonElement jsonElement = jsonObject.get("customeraccounts");
                if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject account = jsonArray.get(i).getAsJsonObject();
                        String contractId = (account.has("contractId") && !account.get("contractId").isJsonNull()) ? account.get("contractId").getAsString() : null;
                        String coreCustomerId = (account.has("coreCustomerId") && !account.get("coreCustomerId").isJsonNull()) ? account.get("coreCustomerId").getAsString() : null;
                        if (!customerAccounts.containsKey(contractId))
                            customerAccounts.put(contractId, new HashMap<>());
                        if (!((Map)customerAccounts.get(contractId)).containsKey(coreCustomerId))
                            ((Map)customerAccounts.get(contractId)).put(coreCustomerId, new HashSet());
                        ((Set<String>)((Map)customerAccounts.get(contractId)).get(coreCustomerId))
                                .add(account.get("Account_id").getAsString());
                    }
                }
            }
        }
    }


    private void updateSignatoryGroupEntry(String customerId, JsonObject jsonObject, Result result, DataControllerRequest request) {
        Map<String, String> loggedInUserInfo = HelperMethods.getCustomerFromAPIDBPIdentityService(request);
        if (HelperMethods.isAuthenticationCheckRequiredForService(loggedInUserInfo))
            return;
        JsonArray signatoryGroups = new JsonArray();
        if (jsonObject.has("signatoryGroups") &&
                !jsonObject.get("signatoryGroups").isJsonNull())
            if (jsonObject.get("signatoryGroups").isJsonArray()) {
                signatoryGroups = jsonObject.get("signatoryGroups").getAsJsonArray();
            } else {
                try {
                    signatoryGroups = (new JsonParser()).parse(jsonObject.get("signatoryGroups").getAsString()).getAsJsonArray();
                } catch (Exception exception) {}
            }
        JSONArray signatoryGroups1 = new JSONArray();
        for (JsonElement element : signatoryGroups) {
            JsonObject signatory = element.getAsJsonObject();
            JSONObject signatory1 = new JSONObject();
            String contractId = signatory.get("contractId").getAsString();
            String cif = signatory.get("cif").getAsString();
            signatory1.put("coreCustomerId", cif);
            signatory1.put("cif", cif);
            signatory1.put("contractId", contractId);
            signatory1.put("customerId", customerId);
            JsonArray groups = signatory.get("groups").getAsJsonArray();
            String signatoryGroupId = "";
            if (groups.size() > 0) {
                for (JsonElement group : groups) {
                    if (group.getAsJsonObject().has("signatoryGroupId") &&
                            !group.getAsJsonObject().get("signatoryGroupId").isJsonNull() &&
                            StringUtils.isNotBlank(group
                                    .getAsJsonObject().get("signatoryGroupId").getAsString()) && group

                            .getAsJsonObject().has("isAssociated") &&
                            !group.getAsJsonObject().get("isAssociated").isJsonNull() &&
                            Boolean.parseBoolean(group
                                    .getAsJsonObject().get("isAssociated").getAsString())) {
                        signatoryGroupId = group.getAsJsonObject().get("signatoryGroupId").getAsString();
                        signatory1.put("signatoryGroupId", signatoryGroupId);
                        signatoryGroups1.put(signatory1);
                    }
                }
                continue;
            }
            signatory1.put("signatoryGroupId", signatoryGroupId);
            signatoryGroups1.put(signatory1);
        }
        if (signatoryGroups1.length() > 0) {
            JSONObject signatory = new JSONObject();
            signatory.put("signatoryGroups", signatoryGroups1);
            Result result2 = ((SignatoryGroupResource)DBPAPIAbstractFactoryImpl.getResource(SignatoryGroupResource.class)).updateSignatoryGroupForInfinityUser(signatory);
            result.addAllDatasets(result2.getAllDatasets());
            result.addAllParams(result2.getAllParams());
            result.addAllRecords(result2.getAllRecords());
        }
    }

}

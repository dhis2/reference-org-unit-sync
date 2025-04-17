<#if headers.DebeziumOperation == 'c'>
    <#assign op = 'creates'>
<#elseif headers.DebeziumOperation == 'u'>
    <#assign op = 'updates'>
<#elseif headers.DebeziumOperation == 'd'>
    <#assign op = 'deletes'>
</#if>

An org unit sync operation requires approval because it ${op} the following fields: ${variables.updatedApprovalFields?join(', ')}

Review link: ${variables.target['dhis2ApiUrl']?remove_ending('api/')?remove_ending('api')}dhis-web-datastore/index.html#/edit/org-unit-sync/${variables.dataStoreKey}

Approve link: http://${variables.hostname}:${variables.camelJbangPlatformHttpPort}/approve?data=${variables.dataQueryParam}

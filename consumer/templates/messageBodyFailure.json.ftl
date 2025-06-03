<#if headers.DebeziumOperation == 'c'>
    <#assign op = 'created'>
<#elseif headers.DebeziumOperation == 'u'>
    <#assign op = 'updated'>
<#elseif headers.DebeziumOperation == 'd'>
    <#assign op = 'deleted'>
<#elseif headers.DebeziumOperation == 'r'>
    <#assign op = 'replicated'>
</#if>

<#assign debeziumDiff><#list headers.DebeziumDiff?split(",") as field>${field},\n</#list></#assign>
<#assign adminUserId = "M5zQapPyTZI"</#assign>

{
    "subject": "[Org Unit Sync App] Error while synchronising '${variables.id}' object in '${headers.DebeziumSourceTable}' table",
    "text": "The '${variables.id}' object in the '${headers.DebeziumSourceTable}' table could not be ${op} because of a technical failure. Please contact tech support."
}

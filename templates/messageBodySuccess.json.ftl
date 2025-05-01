<#if headers.DebeziumOperation == 'c'>
    <#assign op = 'created'>
<#elseif headers.DebeziumOperation == 'u'>
    <#assign op = 'updated'>
<#elseif headers.DebeziumOperation == 'd'>
    <#assign op = 'deleted'>
<#elseif headers.DebeziumOperation == 'r'>
    <#assign op = 'replicated'>
</#if>

<#assign debeziumDiff><#list headers.DebeziumDiff?split(",") as field>${field}\n</#list></#assign>
<#assign adminUserId = "M5zQapPyTZI">

{
    "subject": "[Org Unit Sync App] Synchronised '${variables.id}' object in '${headers.DebeziumSourceTable}' table",
    "text": "The '${variables.id}' object in the '${headers.DebeziumSourceTable}' table was synchronised. It had one or more of the following fields ${op}:\n\n${debeziumDiff}",
    "users": [{
        "id": "${variables.target['messageConversationUserId']}"
    }]
}

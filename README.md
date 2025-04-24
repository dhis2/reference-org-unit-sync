# DRAFT

# Organisation Unit Synchronisation - reference implementation

## What is this implementation?

A frequent requirement in an architecture with multiple DHIS2 instances is keeping the DHIS2 organisation units in sync. Consider a DHIS2 server acting as a master facility registry. Such a server would typically need to have its organisation unit changes replicated to other servers. This reference implementation synchronises the organisation units of a primary DHIS2 server with one or more DHIS2 servers. In particular, it performs one-way synchronisation of the subsequent DHIS2 resources:

* Organisation units: creates, updates, and deletes
* Organisation unit groups: creates and updates
* Organisation unit group sets: creates and updates

This is an example which should be used for reference. It **SHOULD NOT** be used directly in production without adapting it to your local context.

## Overview

The following diagram conceptualises the design of this reference implementation:

![reference implementation design](docs/ref-org-unit-sync-implementation.png)

The org unit sync app is a low-code customisable solution built in [Apache Camel](https://camel.apache.org/) that runs from [JBang](https://www.jbang.dev/). It leverages the [logical replication](https://www.postgresql.org/docs/current/logical-replication.html) feature of PostgreSQL together with [identity replication](https://www.postgresql.org/docs/current/sql-altertable.html#SQL-ALTERTABLE-REPLICA-IDENTITY) to reliably capture DHIS2 organisation unit changes. This means that the following prerequisites need to be satisfied in order to run the reference implementation application:

1. Enabling logical replication in your source of truth DHIS2 database
2. Setting the `REPLICA IDENTITY` of the `organisationunit` source of truth table to `FULL`. 

Once logical replication is enabled and the required replica identity configured, the application can listen for changes to a list of database tables. The tables can be customised but out-of-the-box these are:

* `public.organisationunit`
* `public.orgunitgroup`
* `public.orgunitgroupset`

When the database publishes a change to one of the preceding tables, such as a row insert, the application captures the DHIS2 resource ID from the row before proceeding to fetch by the aforementioned ID the new or updated DHIS2 resource from the primary server via the DHIS2 Web API. This fetched resource is then imported into the configured target DHIS2 servers through their Web APIs.

The Org Unit Sync app notifies the DHIS2 administrator of the target server when it synchronises a resource on the target. A message will appear in the administrator's inbox informing them the resource that was synchronised. The administrator will also be notified when a failure happens during synchronisation. 

### Configuration

The application is configured through one or more properties files and/or command-line arguments. A template of the properties file is found in `application.properties`. You can edit `application.properties` and tailor it your needs. At a minimum, you will need to specify the address details of the primary DHIS2 server and its database together with the address details of at least one target server as shown below:

```properties
source.dhis2DatabaseHostname=192.178.1.6
source.dhis2DatabasePort=5432
source.dhis2DatabaseUser=dhis
source.dhis2DatabasePassword=dhis
source.dhis2DatabaseDbName=dhis2

source.dhis2ApiUrl=https://play.im.dhis2.org/stable-2-41-3/api
source.dhis2ApiPersonalAccessToken=d2pat_x2UluDRx2W0KxmRxT6PnTebe1wjx5Eui3079960708

target.1.dhis2ApiUrl=https://play.im.dhis2.org/stable-2-40-7/api
target.1.dhis2ApiPersonalAccessToken=d2pat_NT03n0uZyjt9HsiYKNDA7KFHLqwy8CVE1103471171
```

While it is strongly recommended to authenticate with DHIS2 using the personal access token, you can choose to authenticate with the DHIS2 servers using HTTP basic access authentication as shown below:

```properties
source.dhis2DatabaseHostname=192.178.1.6
source.dhis2DatabasePort=5432
source.dhis2DatabaseUser=dhis
source.dhis2DatabasePassword=dhis
source.dhis2DatabaseDbName=dhis2

source.dhis2ApiUrl=https://play.im.dhis2.org/stable-2-41-3/api
source.dhis2ApiUsername=admin
source.dhis2ApiPassword=district

target.1.dhis2ApiUrl=https://play.im.dhis2.org/stable-2-40-7/api
target.1.dhis2ApiUsername=admin
target.1.dhis2ApiPassword=district
```

You can specify as many targets as you require but bear in mind that each target must have a corresponding distinct integer index in the property name:

```properties
source.dhis2DatabaseHostname=192.178.1.6
source.dhis2DatabasePort=5432
source.dhis2DatabaseUser=dhis
source.dhis2DatabasePassword=dhis
source.dhis2DatabaseDbName=dhis2

source.dhis2ApiUrl=https://play.im.dhis2.org/stable-2-41-3/api
source.dhis2ApiPersonalAccessToken=d2pat_x2UluDRx2W0KxmRxT6PnTebe1wjx5Eui3079960708

target.1.dhis2ApiUrl=https://play.im.dhis2.org/stable-2-40-7/api
target.1.dhis2ApiPersonalAccessToken=d2pat_NT03n0uZyjt9HsiYKNDA7KFHLqwy8CVE1103471171

target.2.dhis2ApiUrl=https://play.im.dhis2.org/stable-2-39-9/api
target.2.dhis2ApiUsername=admin
target.2.dhis2ApiPassword=district
```

The subsequent table lists the properties that can be configured in the org unit sync app:

|                 **Parameter Name**                 | **Description** |
|:--------------------------------------------------:|:---------------:|
|  camel.component.jms.connection-factory.brokerURL  | TODO            |
|       camel.component.jms.connection-factory       | TODO            |
|             camel.jbang.classpathFiles             | TODO            |
|              camel.jbang.dependencies              | TODO            |
|           camel.jbang.platform-http.port           | TODO            |
|          camel.main.routesIncludePattern           | TODO            |
|          deadLetterChannel.deadLetterUri           | TODO            |
|       deadLetterChannel.maximumRedeliveries        | TODO            |
|       deadLetterChannel.onPrepareFailureRef        | TODO            |
|         deadLetterChannel.redeliveryDelay          | TODO            |
|      deadLetterChannel.useExponentialBackOff       | TODO            |
|                      hostname                      | TODO            |
|                 keyStore.password                  | TODO            |
|                   keyStore.path                    | TODO            |
|              source.dhis2ApiPassword               | TODO            |
|         source.dhis2ApiPersonalAccessToken         | TODO            |
|              source.dhis2ApiUsername               | TODO            |
|                 source.dhis2ApiUrl                 | TODO            |
|             source.dhis2DatabaseDbName             | TODO            |
|            source.dhis2DatabaseHostname            | TODO            |
|            source.dhis2DatabasePassword            | TODO            |
|              source.dhis2DatabasePort              | TODO            |
|              source.dhis2DatabaseUser              | TODO            |
|            source.offsetStorageFileName            | TODO            |
|              source.schemaIncludeList              | TODO            |
|                source.snapshotMode                 | TODO            |
|              source.tableIncludeList               | TODO            |
|            target.[n].camelEndpointUri             | TODO            |
|            target.[n].dhis2ApiPassword             | TODO            |
|       target.[n].dhis2ApiPersonalAccessToken       | TODO            |
|            target.[n].dhis2ApiUsername             | TODO            |
|               target.[n].dhis2ApiUrl               | TODO            |
|          target.[n].fieldsRequireApproval          | TODO            |
|        target.[n].messageConversationUserId        | TODO            |
|                target.[n].idScheme                 | TODO            |
|                target.[n].transform                | TODO            |


#### Full snapshot

By default, the org unit sync app replicates changes from its last recorded position in the source database's write-ahead log. That is, the app does NOT replicate table changes that occurred prior to its first start-up. However, the default behaviour can be altered. Setting the `source.snapshotMode` property to `initial` will force the app to replicate all changes in the table's history before streaming new changes. In `initial` mode, the app will only perform the full snapshot when it is starting up for the very first time. The app will not perform snapshots in subsequent start-ups because it will have recorded its own position in the database write-ahead log.

### Approval

Some scenarios necessitate that the synchronisation of certain field changes be approved by a human. The `target.[n].fieldsRequireApproval` property allows you to specify the fields that must be approved before the synchronisation can be applied. When a field listed in `target.[n].fieldsRequireApproval` changes in the source DHIS2 server, the app will:

1. Create an entry of the draft metadata import in the DHIS2 target data store within the `org-unit-sync` namespace.
2. Open a ticket in the DHIS2 target containing a review link and an approval link. The review link points to the draft metadata import which the ticket assignee can view and edit. The approval link points to the org unit sync app itself, and once clicked, kicks off the synchronisation of the resource. 


### Fault tolerance

Failed synchronised operations in this reference implementation are not lost. The implementation retries the failed synchronisation a few times, with a backoff multiplier increasing the time between each retry, before giving up and storing the failed synchronisation in a JMS [dead letter queue](https://www.enterpriseintegrationpatterns.com/patterns/messaging/DeadLetterChannel.html). Note that a failed synchronisation for a given target server will NOT cause the entire synchronisation process to abort. The application will jump to the next target server should it fail to synchronise the current target server.

The system operator can view the dead letter queue from the management console to inspect the error messages together with the resource IDs of failed synchronisations. By default, the app makes use of an embedded JMS broker, however, it is recommended using a standalone JMS broker for production instead of an embedded one. Once you have a standalone broker running, you will need to set the property `camel.component.jms.connection-factory.brokerURL` to the standalone broker address.

### Adaptation

The organisation unit sync reference implementation should be adapted to fit your local needs. A good understanding of Apache Camel is a prerequisite to customising the application. The DHIS2 developer docs provides an introduction to Apache Camel....

### Target type

While the synchronisation functionality was designed for DHIS2, the synchronisation target can be changed from DHIS2 to any other system. To create a new target type, write a Camel route. You can use `camel/dhis2target.camel.yaml` as a guide to writing a target type. Ensure that the `from` URI is a [`direct` endpoint](https://camel.apache.org/components/next/direct-component.html)

### Transformation

The script transforming the source DHIS2 resource into the target resource can be customised. Each synchronised resource has a corresponding DataSonnet mapping file located in the `datasonnet` directory. For example, the code transforming the organisation unit group JSON from the source into an import bundle that the target can accept is found in `datasonnet/orgUnitGroup.ds`. There is plenty of documentation about DataSonnet that can help you learn the mapping tool.

### Synchronise resource

DHIS2 resources can removed or added according to your context. 

## Quick Start

1. Enable logical replication in the source DHIS2 PostgreSQL database as shown in the [PostgreSQL documentation](https://www.postgresql.org/docs/current/logical-replication-config.html#LOGICAL-REPLICATION-CONFIG-PUBLISHER) to capture table changes. At a minimum, the `wal_level` setting in the PostgreSQL configuration file (i.e., `postgresql.conf`) should be set to `logical`.
2. Alter the `organisationunit` table in the source DHIS2 PostgreSQL database as follows to capture the changed columns: `ALTER TABLE organisationunit REPLICA IDENTITY FULL;`.
3. From the environment where you intend to run the reference implementation:
   1. [Install JBang](https://www.jbang.dev/download/)
   2. Install the [Camel plugin for JBang](https://camel.apache.org/manual/camel-jbang.html#_installation)
   3. Edit `application.properties` accordingly
   4. Execute `camel run application.properties` from the terminal
   5. Monitor the console to ensure that no errors have occurred during start-up
4. Open the maintenance app in the source DHIS2 server and add or update an organisation unit, an organisation group, or an organisation group set. 
5. Allow a few seconds for the synchronisation with the target server/s to occur. 
6. From the admin message inbox of the target DHIS2 server, you should see a new message notifying that a synchronisation happened. 
7. Open the synchronised resource from the maintenance app of the target server and inspect the updated fields.
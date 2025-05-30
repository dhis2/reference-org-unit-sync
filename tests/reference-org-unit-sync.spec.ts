import {
    test,
    expect
} from "@playwright/test";

test.beforeEach(async function({
    request
}) {
    await request.delete("http://localhost:8081/api/dataStore/org-unit-sync", {
        headers: {
            "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q="
        }
    });
});

test("has created org unit synced", async function({
    request
}) {

    const orgUnit = await request.post("http://localhost:8080/api/metadata?importStrategy=CREATE_AND_UPDATE", {
        data: {
            organisationUnits: [{
                code: "ACME",

                id: "b7HFMWjj3im",
                name: "Acme",
                openingDate: "2020-01-01",
                shortName: "Acme"
            }]
        },
        headers: {
            "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q=",
            "Content-Type": "application/json"
        }
    });
    expect(orgUnit.status()).toBe(200);

    await expect.poll(async function() {
        const response = await request.get("http://localhost:8081/api/organisationUnits/b7HFMWjj3im", {
            headers: {
                "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q=",
                "Content-Type": "application/json"
            }
        });
        return response.status();
    }).toBe(404);

    await expect.poll(async function() {
        const response = await request.get("http://localhost:8082/api/organisationUnits/b7HFMWjj3im", {
            headers: {
                "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q=",
                "Content-Type": "application/json"
            }
        });
        return response.status();
    }, {
        timeout: 30000
    }).toBe(200);
});

test("has deleted org unit synced", async function({
    request
}) {

    const orgUnit = await request.post("http://localhost:8080/api/metadata?importStrategy=CREATE_AND_UPDATE", {
        data: {
            organisationUnits: [{
                code: "EVIL_CORP",

                id: "fdc6uOvgoji",
                name: "EvilCorp",
                openingDate: "2020-01-01",
                shortName: "EvilCorp"
            }]
        },
        headers: {
            "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q=",
            "Content-Type": "application/json"
        }
    });
    expect(orgUnit.status()).toBe(200);

    await expect.poll(async function() {
        const response = await request.get("http://localhost:8082/api/organisationUnits/fdc6uOvgoji", {
            headers: {
                "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q=",
                "Content-Type": "application/json"
            }
        });
        return response.status();
    }, {
        timeout: 30000
    }).toBe(200);

    const deletedOrgUnit = await request.delete("http://localhost:8080/api/organisationUnits/fdc6uOvgoji", {
        headers: {
            "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q="
        }
    });
    expect(deletedOrgUnit.status()).toBe(200)

    await expect.poll(async function() {
        const response = await request.get("http://localhost:8082/api/organisationUnits/fdc6uOvgoji", {
            headers: {
                "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q=",
                "Content-Type": "application/json"
            }
        });
        return response.status();
    }, {
        timeout: 30000
    }).toBe(404);
});

test("has created org unit group synced", async function({
    request
}) {

    const orgUnitGroup = await request.post("http://localhost:8080/api/metadata?importStrategy=CREATE_AND_UPDATE", {
        data: {
            organisationUnitGroups: [{
                code: "CHC",
                id: "CXw2yu5fodb",
                name: "CHC",
                shortName: "CHC"
            }]
        },
        headers: {
            "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q=",
            "Content-Type": "application/json"
        }
    });
    expect(orgUnitGroup.status()).toBe(200);

    await expect.poll(async function() {
        const response = await request.get("http://localhost:8081/api/organisationUnitGroups/CXw2yu5fodb", {
            headers: {
                "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q=",
                "Content-Type": "application/json"
            }
        });
        return response.status();
    }, {
        timeout: 30000
    }).toBe(200);

    await expect.poll(async function() {
        const response = await request.get("http://localhost:8082/api/organisationUnitGroups/CXw2yu5fodb", {
            headers: {
                "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q=",
                "Content-Type": "application/json"
            }
        });
        return response.status();
    }, {
        timeout: 30000
    }).toBe(200);
});

test("has created org unit group set synced", async function({
    request
}) {

    const orgUnitGroupSet = await request.post("http://localhost:8080/api/metadata?importStrategy=CREATE_AND_UPDATE", {
        data: {
            organisationUnitGroupSets: [{
                code: "Area",
                id: "uIuxlbV1vRT",
                name: "Area",
                shortName: "Area"
            }]
        },
        headers: {
            "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q=",
            "Content-Type": "application/json"
        }
    });
    expect(orgUnitGroupSet.status()).toBe(200);

    await expect.poll(async function() {
        const response = await request.get("http://localhost:8081/api/organisationUnitGroupSets/uIuxlbV1vRT", {
            headers: {
                "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q=",
                "Content-Type": "application/json"
            }
        });
        return response.status();
    }, {
        timeout: 30000
    }).toBe(200);

    await expect.poll(async function() {
        const response = await request.get("http://localhost:8082/api/organisationUnitGroupSets/uIuxlbV1vRT", {
            headers: {
                "Authorization": "Basic YWRtaW46ZGlzdHJpY3Q=",
                "Content-Type": "application/json"
            }
        });
        return response.status();
    }, {
        timeout: 30000
    }).toBe(200);
});
import { test, expect } from '@playwright/test';

test('has org units synced', async ({ request }) => {

    const orgUnit = await request.post('http://localhost:8080/api/organisationUnits', {
        data: {
          id: 'b7HFMWjj3im',
          name: 'Acme',
          code: 'ACME',
          shortName: 'Acme',
          openingDate: '2020-01-01'
        },
         headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Basic YWRtaW46ZGlzdHJpY3Q=',
            },
    });

    expect(orgUnit.status()).toBe(201)

    await expect.poll(async () => {
      const response = await request.get('http://localhost:8081/api/organisationUnits/b7HFMWjj3im', {
           headers: {
                  'Content-Type': 'application/json',
                  'Authorization': 'Basic YWRtaW46ZGlzdHJpY3Q=',
              }
          });
      return response.status();
    }).toBe(404);

    await expect.poll(async () => {
      const response = await request.get('http://localhost:8082/api/organisationUnits/b7HFMWjj3im', {
           headers: {
                  'Content-Type': 'application/json',
                  'Authorization': 'Basic YWRtaW46ZGlzdHJpY3Q=',
              }
          });
      return response.status();
    }, {
      timeout: 20000,
    }).toBe(200);
});

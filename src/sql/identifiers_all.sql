CREATE TABLE swift.identifiers_all (
    identifier_id        BIGINT IDENTITY PRIMARY KEY,

    -- Import metadata
    import_run_id        BIGINT        NOT NULL,
    source_file          NVARCHAR(255) NOT NULL,

    -- Identity / Hierarchy
    eid                  NVARCHAR(100) NOT NULL,
    parent_eid           NVARCHAR(100) NULL,
    group_eid            NVARCHAR(100) NULL,
    entity_type          NVARCHAR(50)  NULL,

    -- Context
    country_code         NVARCHAR(2)   NULL,
    payment_area_codes   NVARCHAR(100) NULL,

    -- Identifier
    identifier_value     NVARCHAR(200) NOT NULL,   -- z.B. 122244744
    identifier_type      NVARCHAR(50)  NOT NULL,   -- z.B. ABAA / USABA
    iso_clc_type         NVARCHAR(50)  NULL,
    identifier_usage     NVARCHAR(50)  NULL,
    financial_type       NVARCHAR(50)  NULL,
    swift_type           NVARCHAR(50)  NULL,

    -- Relationships
    successor_identifier NVARCHAR(200) NULL,
    domestic_ach_id      NVARCHAR(200) NULL,

    -- Other IDs
    fin_bic              NVARCHAR(50)  NULL,
    finplus_dn           NVARCHAR(200) NULL,
    iban_id              NVARCHAR(50)  NULL,
    iban_bic             NVARCHAR(50)  NULL,

    -- Names
    name                 NVARCHAR(400) NULL,
    alternative_name     NVARCHAR(400) NULL,

    -- Address
    address_line_1       NVARCHAR(400) NULL,
    address_line_2       NVARCHAR(400) NULL,
    address_line_3       NVARCHAR(400) NULL,
    department           NVARCHAR(200) NULL,
    department_type      NVARCHAR(100) NULL,
    sub_department       NVARCHAR(200) NULL,
    street_name          NVARCHAR(200) NULL,
    building_number      NVARCHAR(100) NULL,
    building_name        NVARCHAR(200) NULL,
    floor                NVARCHAR(50)  NULL,
    room                 NVARCHAR(50)  NULL,
    town_name            NVARCHAR(200) NULL,
    town_location_name   NVARCHAR(200) NULL,
    district_name        NVARCHAR(200) NULL,
    post_box             NVARCHAR(100) NULL,
    post_code            NVARCHAR(50)  NULL,
    country_subdiv_name  NVARCHAR(200) NULL,
    country_subdiv_code  NVARCHAR(50)  NULL,
    country_name         NVARCHAR(200) NULL,

    -- Miscellaneous
    language             NVARCHAR(50)  NULL,
    script               NVARCHAR(50)  NULL,
    time_zone            NVARCHAR(50)  NULL,

    -- validity
    valid_from           DATE          NULL,
    valid_to             DATE          NULL
);
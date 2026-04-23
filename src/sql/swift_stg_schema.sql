
IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = 'swift_stg')
    EXEC('CREATE SCHEMA swift_stg');
GO

CREATE TABLE swift_stg.stg_identifiers_all (
    -- technische Metadaten
       stg_id            BIGINT IDENTITY PRIMARY KEY,
       import_run_id     BIGINT NOT NULL,
       import_file       NVARCHAR(255) NOT NULL,
       import_timestamp  DATETIME2 NOT NULL DEFAULT SYSDATETIME(),

    -- Core XSD Felder
       modification_type  CHAR(1)         NOT NULL,
       record_key         NVARCHAR(20)     NOT NULL,
       record_structure   NVARCHAR(100)    NOT NULL,
       record_content_type CHAR(1)         NOT NULL,
       record_status      NVARCHAR(50)     NOT NULL,
       start_date         NVARCHAR(20)     NULL,
       stop_date          NVARCHAR(20)     NULL,

    -- Attribute 1–50 (1:1 aus XSD)
       attribute_1  NVARCHAR(1000) NULL,
       attribute_2  NVARCHAR(1000) NULL,
       attribute_3  NVARCHAR(1000) NULL,
       attribute_4  NVARCHAR(1000) NULL,
       attribute_5  NVARCHAR(1000) NULL,
       attribute_6  NVARCHAR(1000) NULL,
       attribute_7  NVARCHAR(1000) NULL,
       attribute_8  NVARCHAR(1000) NULL,
       attribute_9  NVARCHAR(1000) NULL,
       attribute_10 NVARCHAR(1000) NULL,
       attribute_11 NVARCHAR(1000) NULL,
       attribute_12 NVARCHAR(1000) NULL,
       attribute_13 NVARCHAR(1000) NULL,
       attribute_14 NVARCHAR(1000) NULL,
       attribute_15 NVARCHAR(1000) NULL,
       attribute_16 NVARCHAR(1000) NULL,
       attribute_17 NVARCHAR(1000) NULL,
       attribute_18 NVARCHAR(1000) NULL,
       attribute_19 NVARCHAR(1000) NULL,
       attribute_20 NVARCHAR(1000) NULL,
       attribute_21 NVARCHAR(1000) NULL,
       attribute_22 NVARCHAR(1000) NULL,
       attribute_23 NVARCHAR(1000) NULL,
       attribute_24 NVARCHAR(1000) NULL,
       attribute_25 NVARCHAR(1000) NULL,
       attribute_26 NVARCHAR(1000) NULL,
       attribute_27 NVARCHAR(1000) NULL,
       attribute_28 NVARCHAR(1000) NULL,
       attribute_29 NVARCHAR(1000) NULL,
       attribute_30 NVARCHAR(1000) NULL,
       attribute_31 NVARCHAR(1000) NULL,
       attribute_32 NVARCHAR(1000) NULL,
       attribute_33 NVARCHAR(1000) NULL,
       attribute_34 NVARCHAR(1000) NULL,
       attribute_35 NVARCHAR(1000) NULL,
       attribute_36 NVARCHAR(1000) NULL,
       attribute_37 NVARCHAR(1000) NULL,
       attribute_38 NVARCHAR(1000) NULL,
       attribute_39 NVARCHAR(1000) NULL,
       attribute_40 NVARCHAR(1000) NULL,
       attribute_41 NVARCHAR(1000) NULL,
       attribute_42 NVARCHAR(1000) NULL,
       attribute_43 NVARCHAR(1000) NULL,
       attribute_44 NVARCHAR(1000) NULL,
       attribute_45 NVARCHAR(1000) NULL,
       attribute_46 NVARCHAR(1000) NULL,
       attribute_47 NVARCHAR(1000) NULL,
       attribute_48 NVARCHAR(1000) NULL,
       attribute_49 NVARCHAR(1000) NULL,
       attribute_50 NVARCHAR(1000) NULL
);
GO


/***** Duplicate tables *****/
SELECT TOP 0 * INTO swift_stg.stg_codes_iso             FROM swift_stg.stg_identifiers_all;
SELECT TOP 0 * INTO swift_stg.stg_codes_sepa            FROM swift_stg.stg_identifiers_all;
SELECT TOP 0 * INTO swift_stg.stg_calendars_ctry        FROM swift_stg.stg_identifiers_all;
SELECT TOP 0 * INTO swift_stg.stg_calendars_sepa        FROM swift_stg.stg_identifiers_all;
SELECT TOP 0 * INTO swift_stg.stg_formats_ctry          FROM swift_stg.stg_identifiers_all;
SELECT TOP 0 * INTO swift_stg.stg_formats_sepa          FROM swift_stg.stg_identifiers_all;
SELECT TOP 0 * INTO swift_stg.stg_identifiers_sepa      FROM swift_stg.stg_identifiers_all;
SELECT TOP 0 * INTO swift_stg.stg_identifiers_histall   FROM swift_stg.stg_identifiers_all;
SELECT TOP 0 * INTO swift_stg.stg_identifiers_histsepa  FROM swift_stg.stg_identifiers_all;
SELECT TOP 0 * INTO swift_stg.stg_participants_sepa     FROM swift_stg.stg_identifiers_all;
SELECT TOP 0 * INTO swift_stg.stg_relationships_sepa    FROM swift_stg.stg_identifiers_all;
GO

ALTER TABLE swift_stg.stg_codes_iso ADD CONSTRAINT df_stg_codes_iso_import_timestamp DEFAULT SYSDATETIME() FOR import_timestamp;
ALTER TABLE swift_stg.stg_codes_sepa ADD CONSTRAINT df_stg_codes_sepa_import_timestamp DEFAULT SYSDATETIME() FOR import_timestamp;
ALTER TABLE swift_stg.stg_calendars_ctry ADD CONSTRAINT df_stg_calendars_ctry_import_timestamp DEFAULT SYSDATETIME() FOR import_timestamp;
ALTER TABLE swift_stg.stg_calendars_sepa ADD CONSTRAINT df_stg_calendars_sepa_import_timestamp DEFAULT SYSDATETIME() FOR import_timestamp;
ALTER TABLE swift_stg.stg_formats_ctry ADD CONSTRAINT df_stg_formats_ctry_import_timestamp DEFAULT SYSDATETIME() FOR import_timestamp;
ALTER TABLE swift_stg.stg_formats_sepa ADD CONSTRAINT df_stg_formats_sepa_import_timestamp DEFAULT SYSDATETIME() FOR import_timestamp;
ALTER TABLE swift_stg.stg_identifiers_sepa ADD CONSTRAINT df_stg_identifiers_sepa_import_timestamp DEFAULT SYSDATETIME() FOR import_timestamp;
ALTER TABLE swift_stg.stg_identifiers_histall ADD CONSTRAINT df_stg_identifiers_histall_import_timestamp DEFAULT SYSDATETIME() FOR import_timestamp;
ALTER TABLE swift_stg.stg_identifiers_histsepa ADD CONSTRAINT df_stg_identifiers_histsepa_import_timestamp DEFAULT SYSDATETIME() FOR import_timestamp;
ALTER TABLE swift_stg.stg_participants_sepa ADD CONSTRAINT df_stg_participants_sepa_import_timestamp DEFAULT SYSDATETIME() FOR import_timestamp;
ALTER TABLE swift_stg.stg_relationships_sepa ADD CONSTRAINT df_stg_relationships_sepa_import_timestamp DEFAULT SYSDATETIME() FOR import_timestamp;
GO


CREATE TABLE swift_stg.stg_structure_def
(
    structure_name   VARCHAR(50)  NOT NULL,
    file_version     VARCHAR(10)  NOT NULL,
    record_structure VARCHAR(100) NOT NULL,
    import_run_id    BIGINT       NOT NULL,
    import_file      VARCHAR(255) NOT NULL,
    load_ts          DATETIME2    NOT NULL
);
GO

CREATE TABLE swift_stg.stg_structure_attr
(
    structure_name        VARCHAR(50)  NOT NULL,
    file_version          VARCHAR(10)  NOT NULL,
    record_structure      VARCHAR(100) NOT NULL,
    attribute_no          INT          NOT NULL,
    semantic_name         VARCHAR(100),
    attribute_label       VARCHAR(100),
    attribute_description NVARCHAR(MAX),
    attribute_format      VARCHAR(50),
    mandatory_flag        CHAR(1),
    natural_key_flag      CHAR(1),
    import_run_id         BIGINT       NOT NULL,
    import_file           VARCHAR(255) NOT NULL,
    load_ts               DATETIME2    NOT NULL
);
GO
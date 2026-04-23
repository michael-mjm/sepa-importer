/* =======================================================================
   CREATE SCHEMA
   ======================================================================= */

IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = 'swift')
    EXEC('CREATE SCHEMA swift');
GO


/* =======================================================================
   TABLE TEMPLATE NOTES
   - All master tables use NVARCHAR to preserve original SWIFT Unicode data
   - Legacy views will convert NVARCHAR → VARCHAR (ANSI)
   ======================================================================= */

/* =======================================================================
   1) CALENDARS TABLES
   ======================================================================= */

CREATE TABLE swift.calendars_ctry (
      id INT IDENTITY(1,1) PRIMARY KEY,

    -- Core fields
      modification_type CHAR(1) NOT NULL,        -- A | M | D
      record_key VARCHAR(12) NOT NULL UNIQUE,     -- CA[0-9A-Z]{10}
      record_structure VARCHAR(50) NOT NULL,
      record_content_type CHAR(1) NOT NULL,       -- D | H
      record_status NVARCHAR(20) NOT NULL,

    -- Dates (string because of XSD: "START DATE" / "STOP DATE")
      start_date NVARCHAR(10) NULL,
      stop_date  NVARCHAR(10) NULL,

    -- Attributes 1–50
      attribute_1  NVARCHAR(255) NOT NULL,
      attribute_2  NVARCHAR(255) NOT NULL,
      attribute_3  NVARCHAR(255) NULL,
      attribute_4  NVARCHAR(255) NULL,
      attribute_5  NVARCHAR(255) NULL,
      attribute_6  NVARCHAR(255) NULL,
      attribute_7  NVARCHAR(255) NULL,
      attribute_8  NVARCHAR(255) NULL,
      attribute_9  NVARCHAR(255) NULL,
      attribute_10 NVARCHAR(255) NULL,
      attribute_11 NVARCHAR(255) NULL,
      attribute_12 NVARCHAR(255) NULL,
      attribute_13 NVARCHAR(255) NULL,
      attribute_14 NVARCHAR(255) NULL,
      attribute_15 NVARCHAR(255) NULL,
      attribute_16 NVARCHAR(255) NULL,
      attribute_17 NVARCHAR(255) NULL,
      attribute_18 NVARCHAR(255) NULL,
      attribute_19 NVARCHAR(255) NULL,
      attribute_20 NVARCHAR(255) NULL,
      attribute_21 NVARCHAR(255) NULL,
      attribute_22 NVARCHAR(255) NULL,
      attribute_23 NVARCHAR(255) NULL,
      attribute_24 NVARCHAR(255) NULL,
      attribute_25 NVARCHAR(255) NULL,
      attribute_26 NVARCHAR(255) NULL,
      attribute_27 NVARCHAR(255) NULL,
      attribute_28 NVARCHAR(255) NULL,
      attribute_29 NVARCHAR(255) NULL,
      attribute_30 NVARCHAR(255) NULL,
      attribute_31 NVARCHAR(255) NULL,
      attribute_32 NVARCHAR(255) NULL,
      attribute_33 NVARCHAR(255) NULL,
      attribute_34 NVARCHAR(255) NULL,
      attribute_35 NVARCHAR(255) NULL,
      attribute_36 NVARCHAR(255) NULL,
      attribute_37 NVARCHAR(255) NULL,
      attribute_38 NVARCHAR(255) NULL,
      attribute_39 NVARCHAR(255) NULL,
      attribute_40 NVARCHAR(255) NULL,
      attribute_41 NVARCHAR(255) NULL,
      attribute_42 NVARCHAR(255) NULL,
      attribute_43 NVARCHAR(255) NULL,
      attribute_44 NVARCHAR(255) NULL,
      attribute_45 NVARCHAR(255) NULL,
      attribute_46 NVARCHAR(255) NULL,
      attribute_47 NVARCHAR(255) NULL,
      attribute_48 NVARCHAR(255) NULL,
      attribute_49 NVARCHAR(255) NULL,
      attribute_50 NVARCHAR(255) NULL,

      import_timestamp DATETIME2 DEFAULT SYSDATETIME(),
      import_file NVARCHAR(255)
);
GO


CREATE TABLE swift.calendars_sepa (
    id INT IDENTITY PRIMARY KEY,
    modification_type CHAR(1),
    record_key VARCHAR(30) UNIQUE,
    record_structure VARCHAR(100),
    record_content_type CHAR(1),
    record_status CHAR(1),

    attribute_1 NVARCHAR(255),
    attribute_2 NVARCHAR(255),
    attribute_3 NVARCHAR(255),
    attribute_4 NVARCHAR(255),
    attribute_5 NVARCHAR(255),
    attribute_6 NVARCHAR(255),
    attribute_7 NVARCHAR(255),
    attribute_8 NVARCHAR(255),
    attribute_9 NVARCHAR(255),
    attribute_10 NVARCHAR(255),

    import_timestamp DATETIME2 DEFAULT SYSDATETIME(),
    import_file NVARCHAR(255)
);
GO



/* =======================================================================
   2) CODES TABLES
   ======================================================================= */

CREATE TABLE swift.codes_iso (
    id INT IDENTITY PRIMARY KEY,
    modification_type CHAR(1),
    record_key VARCHAR(30) UNIQUE,
    record_structure VARCHAR(100),
    record_content_type CHAR(1),
    record_status CHAR(1),

    attribute_1 NVARCHAR(255),
    attribute_2 NVARCHAR(255),
    attribute_3 NVARCHAR(255),
    attribute_4 NVARCHAR(255),
    attribute_5 NVARCHAR(255),
    attribute_6 NVARCHAR(255),
    attribute_7 NVARCHAR(255),
    attribute_8 NVARCHAR(255),
    attribute_9 NVARCHAR(255),
    attribute_10 NVARCHAR(255),

    import_timestamp DATETIME2 DEFAULT SYSDATETIME(),
    import_file NVARCHAR(255)
);
GO


CREATE TABLE swift.codes_sepa (
    id INT IDENTITY PRIMARY KEY,
    modification_type CHAR(1),
    record_key VARCHAR(30) UNIQUE,
    record_structure VARCHAR(100),
    record_content_type CHAR(1),
    record_status CHAR(1),

    attribute_1 NVARCHAR(255),
    attribute_2 NVARCHAR(255),
    attribute_3 NVARCHAR(255),
    attribute_4 NVARCHAR(255),
    attribute_5 NVARCHAR(255),
    attribute_6 NVARCHAR(255),
    attribute_7 NVARCHAR(255),
    attribute_8 NVARCHAR(255),
    attribute_9 NVARCHAR(255),
    attribute_10 NVARCHAR(255),

    import_timestamp DATETIME2 DEFAULT SYSDATETIME(),
    import_file NVARCHAR(255)
);
GO



/* =======================================================================
   3) FORMATS TABLES
   ======================================================================= */

CREATE TABLE swift.formats_ctry (
    id INT IDENTITY PRIMARY KEY,
    modification_type CHAR(1),
    record_key VARCHAR(30) UNIQUE,
    record_structure VARCHAR(100),
    record_content_type CHAR(1),
    record_status CHAR(1),

    attribute_1 NVARCHAR(255),
    attribute_2 NVARCHAR(255),
    attribute_3 NVARCHAR(255),
    attribute_4 NVARCHAR(255),
    attribute_5 NVARCHAR(255),
    attribute_6 NVARCHAR(255),
    attribute_7 NVARCHAR(255),
    attribute_8 NVARCHAR(255),
    attribute_9 NVARCHAR(255),
    attribute_10 NVARCHAR(255),
    attribute_11 NVARCHAR(255),
    attribute_12 NVARCHAR(255),
    attribute_13 NVARCHAR(255),
    attribute_14 NVARCHAR(255),
    attribute_15 NVARCHAR(255),
    attribute_16 NVARCHAR(255),

    import_timestamp DATETIME2 DEFAULT SYSDATETIME(),
    import_file NVARCHAR(255)
);
GO


CREATE TABLE swift.formats_sepa (
    id INT IDENTITY PRIMARY KEY,
    modification_type CHAR(1),
    record_key VARCHAR(30) UNIQUE,
    record_structure VARCHAR(100),
    record_content_type CHAR(1),
    record_status CHAR(1),

    attribute_1 NVARCHAR(255),
    attribute_2 NVARCHAR(255),
    attribute_3 NVARCHAR(255),
    attribute_4 NVARCHAR(255),
    attribute_5 NVARCHAR(255),
    attribute_6 NVARCHAR(255),
    attribute_7 NVARCHAR(255),
    attribute_8 NVARCHAR(255),
    attribute_9 NVARCHAR(255),
    attribute_10 NVARCHAR(255),
    attribute_11 NVARCHAR(255),
    attribute_12 NVARCHAR(255),
    attribute_13 NVARCHAR(255),
    attribute_14 NVARCHAR(255),
    attribute_15 NVARCHAR(255),
    attribute_16 NVARCHAR(255),

    import_timestamp DATETIME2 DEFAULT SYSDATETIME(),
    import_file NVARCHAR(255)
);
GO



/* =======================================================================
   4) IDENTIFIERS TABLES – 42 Attributes
   ======================================================================= */

CREATE TABLE swift.identifiers_all (
       id INT IDENTITY(1,1) PRIMARY KEY,

    -- Core fields
       modification_type CHAR(1) NOT NULL,          -- A | M | D
       record_key VARCHAR(12) NOT NULL UNIQUE,       -- ID[0-9A-Z]{10}
       record_structure NVARCHAR(100) NOT NULL,
       record_content_type CHAR(1) NOT NULL,         -- D | H
       record_status NVARCHAR(20) NOT NULL,          -- C | RECORD STATUS

    -- Dates as STRING (XSD allows START DATE / STOP DATE)
       start_date NVARCHAR(10) NULL,
       stop_date  NVARCHAR(10) NULL,

    -- Attributes 1–50 (genericType)
       attribute_1  NVARCHAR(255) NOT NULL,
       attribute_2  NVARCHAR(255) NOT NULL,
       attribute_3  NVARCHAR(255) NOT NULL,
       attribute_4  NVARCHAR(255) NOT NULL,
       attribute_5  NVARCHAR(255) NOT NULL,
       attribute_6  NVARCHAR(255) NULL,
       attribute_7  NVARCHAR(255) NOT NULL,
       attribute_8  NVARCHAR(255) NOT NULL,
       attribute_9  NVARCHAR(255) NULL,
       attribute_10 NVARCHAR(255) NULL,
       attribute_11 NVARCHAR(255) NULL,
       attribute_12 NVARCHAR(255) NULL,
       attribute_13 NVARCHAR(255) NULL,
       attribute_14 NVARCHAR(255) NULL,
       attribute_15 NVARCHAR(255) NULL,
       attribute_16 NVARCHAR(255) NULL,
       attribute_17 NVARCHAR(255) NULL,
       attribute_18 NVARCHAR(255) NULL,
       attribute_19 NVARCHAR(255) NOT NULL,
       attribute_20 NVARCHAR(255) NULL,
       attribute_21 NVARCHAR(255) NULL,
       attribute_22 NVARCHAR(255) NULL,
       attribute_23 NVARCHAR(255) NULL,
       attribute_24 NVARCHAR(255) NULL,
       attribute_25 NVARCHAR(255) NULL,
       attribute_26 NVARCHAR(255) NULL,
       attribute_27 NVARCHAR(255) NULL,
       attribute_28 NVARCHAR(255) NULL,
       attribute_29 NVARCHAR(255) NULL,
       attribute_30 NVARCHAR(255) NULL,
       attribute_31 NVARCHAR(255) NULL,
       attribute_32 NVARCHAR(255) NULL,
       attribute_33 NVARCHAR(255) NULL,
       attribute_34 NVARCHAR(255) NULL,
       attribute_35 NVARCHAR(255) NULL,
       attribute_36 NVARCHAR(255) NULL,
       attribute_37 NVARCHAR(255) NULL,
       attribute_38 NVARCHAR(255) NULL,
       attribute_39 NVARCHAR(255) NOT NULL,
       attribute_40 NVARCHAR(255) NULL,
       attribute_41 NVARCHAR(255) NULL,
       attribute_42 NVARCHAR(255) NULL,
       attribute_43 NVARCHAR(255) NULL,
       attribute_44 NVARCHAR(255) NULL,
       attribute_45 NVARCHAR(255) NULL,
       attribute_46 NVARCHAR(255) NULL,
       attribute_47 NVARCHAR(255) NULL,
       attribute_48 NVARCHAR(255) NULL,
       attribute_49 NVARCHAR(255) NULL,
       attribute_50 NVARCHAR(255) NULL,

       import_timestamp DATETIME2 DEFAULT SYSDATETIME(),
       import_file NVARCHAR(255)
);
GO

/***** Duplicate tables *****/
SELECT TOP 0 * INTO swift.identifiers_sepa     FROM swift.identifiers_all;
SELECT TOP 0 * INTO swift.identifiers_histall  FROM swift.identifiers_all;
SELECT TOP 0 * INTO swift.identifiers_histsepa FROM swift.identifiers_all;
GO



/* =======================================================================
   5) PARTICIPANTS
   ======================================================================= */

CREATE TABLE swift.participants_sepa (
    id INT IDENTITY PRIMARY KEY,
    modification_type CHAR(1),
    record_key VARCHAR(30) UNIQUE,
    record_structure VARCHAR(100),
    record_content_type CHAR(1),
    record_status CHAR(1),

    attribute_1 NVARCHAR(255),
    attribute_2 NVARCHAR(255),
    attribute_3 NVARCHAR(255),
    attribute_4 NVARCHAR(255),
    attribute_5 NVARCHAR(255),
    attribute_6 NVARCHAR(255),
    attribute_7 NVARCHAR(255),
    attribute_8 NVARCHAR(255),
    attribute_9 NVARCHAR(255),
    attribute_10 NVARCHAR(255),
    attribute_11 NVARCHAR(255),
    attribute_12 NVARCHAR(255),
    attribute_13 NVARCHAR(255),
    attribute_14 NVARCHAR(255),
    attribute_15 NVARCHAR(255),
    attribute_16 NVARCHAR(255),
    attribute_17 NVARCHAR(255),
    attribute_18 NVARCHAR(255),
    attribute_19 NVARCHAR(255),
    attribute_20 NVARCHAR(255),

    import_timestamp DATETIME2 DEFAULT SYSDATETIME(),
    import_file NVARCHAR(255)
);
GO



/* =======================================================================
   6) RELATIONSHIPS
   ======================================================================= */

CREATE TABLE swift.relationships_sepa (
    id INT IDENTITY PRIMARY KEY,
    modification_type CHAR(1),
    record_key VARCHAR(30) UNIQUE,
    record_structure VARCHAR(100),
    record_content_type CHAR(1),
    record_status CHAR(1),

    attribute_1 NVARCHAR(255),
    attribute_2 NVARCHAR(255),
    attribute_3 NVARCHAR(255),
    attribute_4 NVARCHAR(255),
    attribute_5 NVARCHAR(255),
    attribute_6 NVARCHAR(255),
    attribute_7 NVARCHAR(255),
    attribute_8 NVARCHAR(255),
    attribute_9 NVARCHAR(255),
    attribute_10 NVARCHAR(255),
    attribute_11 NVARCHAR(255),
    attribute_12 NVARCHAR(255),
    attribute_13 NVARCHAR(255),
    attribute_14 NVARCHAR(255),
    attribute_15 NVARCHAR(255),
    attribute_16 NVARCHAR(255),
    attribute_17 NVARCHAR(255),
    attribute_18 NVARCHAR(255),
    attribute_19 NVARCHAR(255),
    attribute_20 NVARCHAR(255),

    import_timestamp DATETIME2 DEFAULT SYSDATETIME(),
    import_file NVARCHAR(255)
);
GO



/* =======================================================================
   7) STRUCTURES (Metadata)
   ======================================================================= */

CREATE TABLE swift.structures (
    id INT IDENTITY PRIMARY KEY,

    file_name_prefix NVARCHAR(50),
    file_version NVARCHAR(20),
    record_structure_name NVARCHAR(100),
    file_header_attribute NVARCHAR(255),
    record_header_attribute NVARCHAR(255),

    attribute_description NVARCHAR(MAX),
    attribute_format NVARCHAR(100),
    list_of_values NVARCHAR(MAX),

    unique_natural_key BIT,
    always_present BIT,

    import_timestamp DATETIME2 DEFAULT SYSDATETIME(),
    import_file NVARCHAR(255)
);
GO





/* =======================================================================
   LEGACY VIEWS (ANSI only)
   - Converts NVARCHAR → VARCHAR
   - Only fields relevant for legacy apps are included
   ======================================================================= */

CREATE VIEW dbo.v_swift_participants_legacy AS
SELECT
    record_key AS bank_id,
    CAST(attribute_2 AS VARCHAR(20)) AS bic,
    CAST(attribute_6 AS VARCHAR(100)) AS bank_name,
    CAST(attribute_9 AS VARCHAR(50)) AS city,
    CAST(attribute_10 AS VARCHAR(50)) AS region
FROM swift.participants_sepa
WHERE record_content_type = 'D';
GO


CREATE VIEW dbo.v_swift_identifiers_all_legacy AS
SELECT
    record_key,
    CAST(attribute_7  AS VARCHAR(50)) AS id_code,
    CAST(attribute_19 AS VARCHAR(100)) AS name_ascii,
    CAST(attribute_32 AS VARCHAR(50)) AS town_ascii,
    CAST(attribute_36 AS VARCHAR(20)) AS postcode_ascii,
    CAST(attribute_39 AS VARCHAR(50)) AS country_ascii
FROM swift.identifiers_all
WHERE record_content_type = 'D';
GO


CREATE VIEW dbo.v_swift_calendars_ctry_legacy AS
SELECT
    record_key,
    CAST(attribute_1 AS VARCHAR(20)) AS date_value,
    CAST(attribute_2 AS VARCHAR(5))  AS country,
    CAST(attribute_3 AS VARCHAR(50)) AS country_name,
    CAST(attribute_6 AS VARCHAR(10)) AS holiday_type
FROM swift.calendars_ctry
WHERE record_content_type = 'D';
GO


CREATE VIEW dbo.v_swift_formats_sepa_legacy AS
SELECT
    record_key,
    CAST(attribute_1 AS VARCHAR(5)) AS country_code,
    CAST(attribute_3 AS VARCHAR(20)) AS format_type,
    CAST(attribute_7 AS VARCHAR(100)) AS example_value,
    CAST(attribute_10 AS VARCHAR(255)) AS regex_pattern
FROM swift.formats_sepa
WHERE record_content_type = 'D';
GO


CREATE VIEW dbo.v_swift_codes_sepa_legacy AS
SELECT
    record_key,
    CAST(attribute_2 AS VARCHAR(5)) AS country_code,
    CAST(attribute_3 AS VARCHAR(50)) AS country_name,
    CAST(attribute_8 AS VARCHAR(10)) AS currency
FROM swift.codes_sepa
WHERE record_content_type = 'D';
GO


/* =======================================================================
   Dead Letter Queue for failed Records
   ======================================================================= */

CREATE TABLE swift.dead_letter_queue (
    dlq_id           BIGINT IDENTITY(1,1) PRIMARY KEY,

    -- Batch-Context
    import_run_id    BIGINT        NOT NULL,
    source_table     NVARCHAR(100) NOT NULL,
    domain           NVARCHAR(50)  NULL,

    -- Record-Identification
    record_key       NVARCHAR(20)  NULL,
    modification_type CHAR(1)      NULL,

    -- Full payload (replay-enabled)
    payload          NVARCHAR(MAX) NOT NULL,

    -- Error details
    error_type       NVARCHAR(200) NULL,
    error_message    NVARCHAR(1000) NOT NULL,

    -- Lifecycle
    status           VARCHAR(20)   NOT NULL DEFAULT 'NEW',
    retry_count      INT           NOT NULL DEFAULT 0,

    -- Timestamps
    created_at       DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    last_retry_at    DATETIME2     NULL,
    resolved_at      DATETIME2     NULL
);
GO

CREATE INDEX idx_dlq_status
    ON swift.dead_letter_queue (status);

CREATE INDEX idx_dlq_import_run
    ON swift.dead_letter_queue (import_run_id);

CREATE INDEX idx_dlq_domain
    ON swift.dead_letter_queue (domain);

CREATE INDEX idx_dlq_record_key
    ON swift.dead_letter_queue (record_key);


/* =======================================================================
   Import Log table
   ======================================================================= */

CREATE TABLE swift.import_log (
                                  LogId BIGINT IDENTITY(1,1) PRIMARY KEY,
                                  FileName NVARCHAR(255),
                                  ProcessedRows INT,
                                  ErrorCount INT,
                                  StartTime DATETIME2 DEFAULT GETDATE(),
                                  EndTime DATETIME2,
                                  Status NVARCHAR(50),
                                  JobExecutionId BIGINT NULL
);
GO


ALTER TABLE swift.import_log
    ADD CONSTRAINT FK_import_log_job_execution
        FOREIGN KEY (JobExecutionId)
            REFERENCES batch.BATCH_JOB_EXECUTION(JOB_EXECUTION_ID);
GO


/* =======================================================================
   END OF FILE
   ======================================================================= */
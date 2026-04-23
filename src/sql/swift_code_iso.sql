CREATE TABLE swift.codes_iso (
     code_set_group    NVARCHAR(100) NOT NULL,  -- e.g. ISO20022 EXTERNAL CODE SET
     code_set_name     NVARCHAR(128) NOT NULL,  -- e.g. COUNTRY, ExternalPurpose1Code
     code              NVARCHAR(64)  NOT NULL,  -- ISO-Code
     source_record_key NVARCHAR(20) NOT NULL,

     short_name        NVARCHAR(128),
     long_name         NVARCHAR(512),
     description       NVARCHAR(1024),

     valid_from        DATE,
     valid_to          DATE,
     source_file       NVARCHAR(255),

     CONSTRAINT uq_swift_codes_iso
         UNIQUE (
                 code_set_group,
                 code_set_name,
                 code,
                 source_record_key
             )
);
GO


CREATE INDEX idx_codes_iso_lookup
    ON swift.codes_iso (code_set_name, code)
    INCLUDE (short_name, long_name);
GO
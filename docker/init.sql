CREATE TABLE  IF NOT EXISTS public.persons (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	person_azure_id varchar(50) NULL,
	creation_time timestamptz NOT NULL,
	"name" varchar(50) NOT NULL,
    image_name varchar(100) NOT NULL,
    azure_id varchar(80) NULL,
	CONSTRAINT persons_pk PRIMARY KEY (id)
);



CREATE TABLE IF NOT EXISTS public.matches (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	upload_time timestamptz NOT NULL,
	image_name varchar(100) NOT NULL,
	person_id int8,
	CONSTRAINT matches_fk FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE SET NULL
);





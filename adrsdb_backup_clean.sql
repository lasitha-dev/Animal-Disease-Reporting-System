--
-- PostgreSQL database dump
--


-- Dumped from database version 18.0
-- Dumped by pg_dump version 18.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: animal_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.animal_types (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    description text,
    is_active boolean NOT NULL,
    type_name character varying(50) NOT NULL,
    updated_at timestamp(6) without time zone
);


--
-- Name: animals; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.animals (
    id uuid NOT NULL,
    age_months integer,
    age_years integer,
    breed character varying(50),
    created_at timestamp(6) without time zone NOT NULL,
    health_status character varying(20),
    is_active boolean NOT NULL,
    sex character varying(10),
    tag_number character varying(50),
    updated_at timestamp(6) without time zone,
    animal_type_id uuid NOT NULL,
    created_by bigint,
    farm_id uuid NOT NULL,
    updated_by bigint,
    CONSTRAINT animals_sex_check CHECK (((sex)::text = ANY ((ARRAY['MALE'::character varying, 'FEMALE'::character varying, 'UNKNOWN'::character varying])::text[])))
);


--
-- Name: disease_animal_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.disease_animal_types (
    disease_id uuid NOT NULL,
    animal_type_id uuid NOT NULL
);


--
-- Name: disease_reports; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.disease_reports (
    id uuid NOT NULL,
    confirmed_at timestamp(6) without time zone,
    created_at timestamp(6) without time zone NOT NULL,
    diagnosis text,
    is_confirmed boolean NOT NULL,
    notes text,
    outcome character varying(20),
    report_date date NOT NULL,
    symptoms text,
    treatment text,
    updated_at timestamp(6) without time zone,
    confirmed_by bigint,
    disease_id uuid NOT NULL,
    farm_id uuid NOT NULL,
    reported_by bigint NOT NULL,
    affected_count integer,
    image_path character varying(255),
    animal_type_id uuid NOT NULL,
    override_disease_name character varying(255),
    override_severity character varying(20),
    override_description text,
    override_notifiable boolean,
    CONSTRAINT disease_reports_outcome_check CHECK (((outcome)::text = ANY ((ARRAY['RECOVERED'::character varying, 'DIED'::character varying, 'ONGOING'::character varying, 'EUTHANIZED'::character varying])::text[])))
);


--
-- Name: COLUMN disease_reports.override_disease_name; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.disease_reports.override_disease_name IS 'Vet-specified disease name override for this report only';


--
-- Name: COLUMN disease_reports.override_severity; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.disease_reports.override_severity IS 'Vet-specified severity override (LOW, MEDIUM, HIGH, CRITICAL) for this report only';


--
-- Name: COLUMN disease_reports.override_description; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.disease_reports.override_description IS 'Vet-specified description override for this report only';


--
-- Name: COLUMN disease_reports.override_notifiable; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.disease_reports.override_notifiable IS 'Vet-specified notifiable status override for this report only';


--
-- Name: diseases; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.diseases (
    id uuid NOT NULL,
    affected_animal_types text[],
    created_at timestamp(6) without time zone NOT NULL,
    description text,
    disease_code character varying(20),
    disease_name character varying(100) NOT NULL,
    is_active boolean NOT NULL,
    is_notifiable boolean NOT NULL,
    severity character varying(20),
    updated_at timestamp(6) without time zone,
    created_by bigint,
    updated_by bigint,
    animal_type_id uuid,
    symptoms text,
    treatment text,
    created_by_vet boolean DEFAULT false NOT NULL,
    CONSTRAINT diseases_severity_check CHECK (((severity)::text = ANY ((ARRAY['LOW'::character varying, 'MEDIUM'::character varying, 'HIGH'::character varying, 'CRITICAL'::character varying])::text[])))
);


--
-- Name: COLUMN diseases.created_by_vet; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON COLUMN public.diseases.created_by_vet IS 'True if disease was created by a vet via the "Other" option during disease reporting';


--
-- Name: farm_animals; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.farm_animals (
    id uuid NOT NULL,
    count integer NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone,
    animal_type_id uuid NOT NULL,
    farm_id uuid NOT NULL
);


--
-- Name: farm_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.farm_types (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    description text,
    is_active boolean NOT NULL,
    type_name character varying(50) NOT NULL,
    updated_at timestamp(6) without time zone,
    created_by bigint,
    updated_by bigint
);


--
-- Name: farms; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.farms (
    id uuid NOT NULL,
    address text NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    district character varying(50) NOT NULL,
    farm_name character varying(100) NOT NULL,
    gps_latitude numeric(10,8),
    gps_longitude numeric(11,8),
    is_active boolean NOT NULL,
    owner_contact character varying(20),
    owner_name character varying(100),
    province character varying(50) NOT NULL,
    total_animals integer,
    updated_at timestamp(6) without time zone,
    created_by bigint,
    farm_type_id uuid NOT NULL,
    updated_by bigint,
    description text
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    active boolean NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    email character varying(100) NOT NULL,
    first_name character varying(50) NOT NULL,
    last_login timestamp(6) without time zone,
    last_name character varying(50) NOT NULL,
    password character varying(255) NOT NULL,
    phone_number character varying(20),
    role character varying(20) NOT NULL,
    updated_at timestamp(6) without time zone,
    username character varying(50) NOT NULL,
    district character varying(30),
    province character varying(30),
    CONSTRAINT users_district_check CHECK (((district)::text = ANY ((ARRAY['JAFFNA'::character varying, 'KILINOCHCHI'::character varying, 'MANNAR'::character varying, 'MULLAITIVU'::character varying, 'VAVUNIYA'::character varying, 'PUTTALAM'::character varying, 'KURUNEGALA'::character varying, 'GAMPAHA'::character varying, 'COLOMBO'::character varying, 'KALUTARA'::character varying, 'ANURADHAPURA'::character varying, 'POLONNARUWA'::character varying, 'MATALE'::character varying, 'KANDY'::character varying, 'NUWARA_ELIYA'::character varying, 'KEGALLE'::character varying, 'RATNAPURA'::character varying, 'TRINCOMALEE'::character varying, 'BATTICALOA'::character varying, 'AMPARA'::character varying, 'BADULLA'::character varying, 'MONARAGALA'::character varying, 'HAMBANTOTA'::character varying, 'MATARA'::character varying, 'GALLE'::character varying])::text[]))),
    CONSTRAINT users_province_check CHECK (((province)::text = ANY ((ARRAY['NORTHERN'::character varying, 'NORTH_WESTERN'::character varying, 'WESTERN'::character varying, 'NORTH_CENTRAL'::character varying, 'CENTRAL'::character varying, 'SABARAGAMUWA'::character varying, 'EASTERN'::character varying, 'UVA'::character varying, 'SOUTHERN'::character varying])::text[]))),
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'VETERINARY_OFFICER'::character varying])::text[])))
);


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: animal_types; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.animal_types (id, created_at, description, is_active, type_name, updated_at) FROM stdin;
1c394491-78dc-46e8-bcfc-150ab9b921c2	2025-10-29 11:35:45.330498	Domestic bovine animals including cows, bulls, and calves	t	Cattle	\N
e9ca87bf-8df9-47da-a19f-15333ec8be1d	2025-10-29 11:35:45.330498	Water buffalo used for dairy and agriculture	t	Buffalo	\N
1b67f848-bd44-48cc-a998-4692d5ece6a7	2025-10-29 11:35:45.330498	Domestic goats for meat and milk production	t	Goat	\N
ac6a4716-84ff-4963-bee9-2de953e4c362	2025-10-29 11:35:45.330498	Domestic sheep for wool and meat	t	Sheep	\N
a6dcec83-85a4-479d-93c1-ec78a692e14a	2025-10-29 11:35:45.330498	Domestic swine for meat production	t	Pig	\N
e72c0140-5e78-4e29-9292-52406f7c300b	2025-10-29 11:35:45.330498	Domestic fowl including chickens, ducks, and turkeys	t	Poultry	\N
\.


--
-- Data for Name: animals; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.animals (id, age_months, age_years, breed, created_at, health_status, is_active, sex, tag_number, updated_at, animal_type_id, created_by, farm_id, updated_by) FROM stdin;
\.


--
-- Data for Name: disease_animal_types; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.disease_animal_types (disease_id, animal_type_id) FROM stdin;
cf3804ee-9bf0-4a7f-b823-497947333e55	1c394491-78dc-46e8-bcfc-150ab9b921c2
cf3804ee-9bf0-4a7f-b823-497947333e55	e9ca87bf-8df9-47da-a19f-15333ec8be1d
fdf3e6af-b7fc-4911-8de3-2383a965b8b1	1c394491-78dc-46e8-bcfc-150ab9b921c2
fdf3e6af-b7fc-4911-8de3-2383a965b8b1	e9ca87bf-8df9-47da-a19f-15333ec8be1d
bcc0d295-8753-4703-9597-64c62c5ba826	1c394491-78dc-46e8-bcfc-150ab9b921c2
bcc0d295-8753-4703-9597-64c62c5ba826	e9ca87bf-8df9-47da-a19f-15333ec8be1d
8bdc507b-a611-4d97-ac06-60f75634994d	1c394491-78dc-46e8-bcfc-150ab9b921c2
8bdc507b-a611-4d97-ac06-60f75634994d	e9ca87bf-8df9-47da-a19f-15333ec8be1d
58ec9ea5-b765-4da0-bf67-2e7ce0705419	1c394491-78dc-46e8-bcfc-150ab9b921c2
58ec9ea5-b765-4da0-bf67-2e7ce0705419	e9ca87bf-8df9-47da-a19f-15333ec8be1d
245918b9-30fb-4a0a-9c95-d6b2cb6e2858	1c394491-78dc-46e8-bcfc-150ab9b921c2
245918b9-30fb-4a0a-9c95-d6b2cb6e2858	e9ca87bf-8df9-47da-a19f-15333ec8be1d
2d9736c2-035e-4fac-8229-9aff2fc1703d	1c394491-78dc-46e8-bcfc-150ab9b921c2
2d9736c2-035e-4fac-8229-9aff2fc1703d	e9ca87bf-8df9-47da-a19f-15333ec8be1d
672a57ab-0a53-40c8-a8a8-3c7020fec0d5	1c394491-78dc-46e8-bcfc-150ab9b921c2
672a57ab-0a53-40c8-a8a8-3c7020fec0d5	e9ca87bf-8df9-47da-a19f-15333ec8be1d
2578aef4-45e4-4e92-924c-9499284598b8	1c394491-78dc-46e8-bcfc-150ab9b921c2
2578aef4-45e4-4e92-924c-9499284598b8	e9ca87bf-8df9-47da-a19f-15333ec8be1d
199ee0a5-ba6f-4eb1-ad4f-c3b83d772ca5	1c394491-78dc-46e8-bcfc-150ab9b921c2
199ee0a5-ba6f-4eb1-ad4f-c3b83d772ca5	e9ca87bf-8df9-47da-a19f-15333ec8be1d
5cf7fe1a-052d-4617-9065-438c3433d64b	1c394491-78dc-46e8-bcfc-150ab9b921c2
5cf7fe1a-052d-4617-9065-438c3433d64b	e9ca87bf-8df9-47da-a19f-15333ec8be1d
06b23a37-20da-4d2e-8f7e-90c3c52ba852	1c394491-78dc-46e8-bcfc-150ab9b921c2
06b23a37-20da-4d2e-8f7e-90c3c52ba852	e9ca87bf-8df9-47da-a19f-15333ec8be1d
c338f40a-51e0-471f-8bc2-2f8ae29504e3	1c394491-78dc-46e8-bcfc-150ab9b921c2
c338f40a-51e0-471f-8bc2-2f8ae29504e3	e9ca87bf-8df9-47da-a19f-15333ec8be1d
b5b40acc-e40e-48fd-b8dd-9b7b32090f1f	ac6a4716-84ff-4963-bee9-2de953e4c362
b5b40acc-e40e-48fd-b8dd-9b7b32090f1f	1b67f848-bd44-48cc-a998-4692d5ece6a7
eb0a8567-81ca-41c8-bbfe-f574d73e3f45	ac6a4716-84ff-4963-bee9-2de953e4c362
eb0a8567-81ca-41c8-bbfe-f574d73e3f45	1b67f848-bd44-48cc-a998-4692d5ece6a7
85bd2b18-3be5-4746-b953-cd31214b7474	ac6a4716-84ff-4963-bee9-2de953e4c362
85bd2b18-3be5-4746-b953-cd31214b7474	1c394491-78dc-46e8-bcfc-150ab9b921c2
85bd2b18-3be5-4746-b953-cd31214b7474	1b67f848-bd44-48cc-a998-4692d5ece6a7
85bd2b18-3be5-4746-b953-cd31214b7474	e9ca87bf-8df9-47da-a19f-15333ec8be1d
6545334d-7c26-4d3e-a99e-962309ab3d4d	ac6a4716-84ff-4963-bee9-2de953e4c362
6545334d-7c26-4d3e-a99e-962309ab3d4d	1b67f848-bd44-48cc-a998-4692d5ece6a7
9b4451f3-bce2-4682-a970-770d7c467724	ac6a4716-84ff-4963-bee9-2de953e4c362
9b4451f3-bce2-4682-a970-770d7c467724	1b67f848-bd44-48cc-a998-4692d5ece6a7
ac25085a-4e96-400e-b45a-5a0b6cfe3ca5	ac6a4716-84ff-4963-bee9-2de953e4c362
ac25085a-4e96-400e-b45a-5a0b6cfe3ca5	1b67f848-bd44-48cc-a998-4692d5ece6a7
8f17b7bb-9525-4ab6-9049-e6d034ec8c04	ac6a4716-84ff-4963-bee9-2de953e4c362
8f17b7bb-9525-4ab6-9049-e6d034ec8c04	1b67f848-bd44-48cc-a998-4692d5ece6a7
9078897e-aa84-4d26-bf24-effcad6f2d3b	ac6a4716-84ff-4963-bee9-2de953e4c362
9078897e-aa84-4d26-bf24-effcad6f2d3b	1b67f848-bd44-48cc-a998-4692d5ece6a7
340a0cd0-28b9-4e2a-b30e-5b7f56e51f48	ac6a4716-84ff-4963-bee9-2de953e4c362
340a0cd0-28b9-4e2a-b30e-5b7f56e51f48	1b67f848-bd44-48cc-a998-4692d5ece6a7
a3cac7e7-00ba-42f9-a0b9-54f72c57836c	a6dcec83-85a4-479d-93c1-ec78a692e14a
9e2a8bce-01ef-46a1-a969-c652aaba0581	a6dcec83-85a4-479d-93c1-ec78a692e14a
91e8d50b-f31e-40c9-abaf-af46152b8be2	a6dcec83-85a4-479d-93c1-ec78a692e14a
b8c92bc3-7ac0-4307-a633-fe59cae56450	a6dcec83-85a4-479d-93c1-ec78a692e14a
991d987c-526e-435f-865e-384d5a9bf5e2	a6dcec83-85a4-479d-93c1-ec78a692e14a
8cf0449d-3b2c-42ed-b511-f4130d8983e3	a6dcec83-85a4-479d-93c1-ec78a692e14a
8cf0449d-3b2c-42ed-b511-f4130d8983e3	ac6a4716-84ff-4963-bee9-2de953e4c362
8cf0449d-3b2c-42ed-b511-f4130d8983e3	1c394491-78dc-46e8-bcfc-150ab9b921c2
8cf0449d-3b2c-42ed-b511-f4130d8983e3	1b67f848-bd44-48cc-a998-4692d5ece6a7
8cf0449d-3b2c-42ed-b511-f4130d8983e3	e9ca87bf-8df9-47da-a19f-15333ec8be1d
8f5cd124-ee71-4092-bb51-fea523ceaf21	a6dcec83-85a4-479d-93c1-ec78a692e14a
8f5cd124-ee71-4092-bb51-fea523ceaf21	ac6a4716-84ff-4963-bee9-2de953e4c362
8f5cd124-ee71-4092-bb51-fea523ceaf21	1c394491-78dc-46e8-bcfc-150ab9b921c2
8f5cd124-ee71-4092-bb51-fea523ceaf21	1b67f848-bd44-48cc-a998-4692d5ece6a7
8f5cd124-ee71-4092-bb51-fea523ceaf21	e9ca87bf-8df9-47da-a19f-15333ec8be1d
75631f6d-7d03-42c9-b254-a3e6ccdf7317	a6dcec83-85a4-479d-93c1-ec78a692e14a
38e145ce-99c0-4484-8963-ff5b63421ecd	a6dcec83-85a4-479d-93c1-ec78a692e14a
38e145ce-99c0-4484-8963-ff5b63421ecd	1c394491-78dc-46e8-bcfc-150ab9b921c2
38e145ce-99c0-4484-8963-ff5b63421ecd	e9ca87bf-8df9-47da-a19f-15333ec8be1d
eb847972-3d56-49dc-a88c-d7a87aace91a	a6dcec83-85a4-479d-93c1-ec78a692e14a
96ce7475-1c81-4f05-96c8-d8dd39742790	a6dcec83-85a4-479d-93c1-ec78a692e14a
91a69ae1-f43f-4947-b319-069366b7562e	a6dcec83-85a4-479d-93c1-ec78a692e14a
b3e1a9eb-5973-4d08-8897-3f20350abd87	e72c0140-5e78-4e29-9292-52406f7c300b
5b01cd9b-4d32-4c11-95af-f59b5468ee20	e72c0140-5e78-4e29-9292-52406f7c300b
fe594c55-a6b2-4fc0-a2b9-d5683e2a1402	e72c0140-5e78-4e29-9292-52406f7c300b
0ca048e2-190b-43d6-9b37-b869aefeb8f6	e72c0140-5e78-4e29-9292-52406f7c300b
991b888d-abc4-4480-a2ac-18494b1295ba	e72c0140-5e78-4e29-9292-52406f7c300b
154d5939-f92d-4764-8f94-604f53f70eec	e72c0140-5e78-4e29-9292-52406f7c300b
6f5ff598-88a7-4d69-b3b6-0c26e78fd79e	e72c0140-5e78-4e29-9292-52406f7c300b
fe2ea41b-9d2c-4bbc-b91b-572489d1161f	e72c0140-5e78-4e29-9292-52406f7c300b
c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	e72c0140-5e78-4e29-9292-52406f7c300b
24b41530-7dc0-4462-b00e-19da459eb7b3	a6dcec83-85a4-479d-93c1-ec78a692e14a
24b41530-7dc0-4462-b00e-19da459eb7b3	e72c0140-5e78-4e29-9292-52406f7c300b
eb016295-5bd7-4420-9aca-a8cbc8ac0a0b	e72c0140-5e78-4e29-9292-52406f7c300b
9a664202-fb76-4aa5-9a77-b96c3ddb8366	ac6a4716-84ff-4963-bee9-2de953e4c362
9a664202-fb76-4aa5-9a77-b96c3ddb8366	1b67f848-bd44-48cc-a998-4692d5ece6a7
9a664202-fb76-4aa5-9a77-b96c3ddb8366	e72c0140-5e78-4e29-9292-52406f7c300b
0e3f6279-076c-452b-8268-3156d7c82990	e72c0140-5e78-4e29-9292-52406f7c300b
4b84a8fe-6731-46e2-baf0-1f563dcd855c	ac6a4716-84ff-4963-bee9-2de953e4c362
4b84a8fe-6731-46e2-baf0-1f563dcd855c	1b67f848-bd44-48cc-a998-4692d5ece6a7
4b84a8fe-6731-46e2-baf0-1f563dcd855c	e72c0140-5e78-4e29-9292-52406f7c300b
\.


--
-- Data for Name: disease_reports; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.disease_reports (id, confirmed_at, created_at, diagnosis, is_confirmed, notes, outcome, report_date, symptoms, treatment, updated_at, confirmed_by, disease_id, farm_id, reported_by, affected_count, image_path, animal_type_id, override_disease_name, override_severity, override_description, override_notifiable) FROM stdin;
45b6ab68-737e-44ae-bd74-260107e8f4c9	\N	2026-01-26 15:55:08.907168		f		DIED	2020-01-26			2026-01-26 15:55:08.907168	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	751e611c-3d91-4f68-bff1-0e233892c273	5	30	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
1ee615d5-b4c9-4076-9898-2498f5813d46	\N	2026-01-26 15:55:59.923795		f		ONGOING	2020-01-26			2026-01-26 15:55:59.923795	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	57976916-ef68-4321-ae0d-7374bde3d8c4	5	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
8cc54f34-2965-486d-ba72-4e15f0e6ad74	\N	2026-01-26 15:57:22.980782		f		DIED	2020-10-26			2026-01-26 15:57:22.980782	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	54ecb014-80f6-417f-9cf4-60720e3c142e	5	5	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
3cfac9f3-f5b2-492f-a04c-24515fde7aea	\N	2026-01-26 15:58:13.213464		f		DIED	2020-07-26			2026-01-26 15:58:13.213464	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	77c4151e-555c-492b-8753-ebeeb004b63f	5	12	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
5c5294eb-2678-4d11-8836-45fe137b7e4b	\N	2026-01-26 15:59:58.372439		f		DIED	2026-01-26			2026-01-26 15:59:58.372439	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	57976916-ef68-4321-ae0d-7374bde3d8c4	5	2	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
36b8b69f-9c45-408a-9af7-44db17946cc5	\N	2026-01-26 16:30:54.917853		f		DIED	2020-01-26			2026-01-26 16:30:54.917853	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	ded4619c-e827-4d71-8403-438b675e970d	5	5	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
f8c4e4f0-799b-400a-b692-fa7a1e863554	\N	2026-01-26 16:32:54.485985		f		DIED	2020-01-26			2026-01-26 16:32:54.485985	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	f7a3b47a-53a6-4e39-a3c5-00ce53d5f1fc	5	205	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
1eb63c43-f1a2-4e60-9180-f2e91b78db1d	\N	2026-01-26 16:33:43.257817		f		DIED	2020-04-26			2026-01-26 16:33:43.257817	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	c217e01f-1a79-4952-bdd6-29d111a2a48d	5	5	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
a2bb22cc-0bf2-4a1d-9fcb-fe73ae08762d	\N	2026-01-26 16:34:11.085815		f		DIED	2020-10-26			2026-01-26 16:34:11.085815	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	e6c2115f-6339-480c-ac11-1e3fb8178ca1	5	7	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
09b13b4d-28be-42f9-b229-7a904f63435b	\N	2026-01-26 16:35:05.775892		f		ONGOING	2020-01-26			2026-01-26 16:35:05.775892	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	0426d7c5-9cb9-44b6-92db-1423fa063ed1	5	27	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
ce39c620-71be-40e9-bf13-cb8f9fbb714b	\N	2026-01-26 16:36:33.039042		f		ONGOING	2020-01-26			2026-01-26 16:36:33.039042	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	c84ae897-1b68-48e0-8c32-84504f8b89e8	5	310	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
4e41cf6a-adbb-4c9e-b622-19b99eff37db	\N	2026-01-26 16:37:14.959095		f		ONGOING	2020-10-26			2026-01-26 16:37:14.959095	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	c217e01f-1a79-4952-bdd6-29d111a2a48d	5	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
3b247a76-eed7-4609-a419-3b55d2567da0	\N	2026-01-26 16:54:31.468225		f		DIED	2020-01-26			2026-01-26 16:54:31.468225	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	d4b09b0c-e306-48a9-a4aa-32e85828a9b8	5	2	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
daa4552c-e242-42a0-ae25-e1ee8168341f	\N	2026-01-26 16:55:04.388271		f		ONGOING	2020-01-26			2026-01-26 16:55:04.388271	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	b79bc8ee-2a84-4b84-a477-33f5dd8e98c4	5	40	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
b5b3ce1f-3805-4391-9a12-7f4395db75af	\N	2026-01-26 16:56:01.491864		f		ONGOING	2020-01-26			2026-01-26 16:56:01.491864	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	e93fc0bb-f560-46a9-add6-a0cd755bd0f7	5	4	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
a89236d6-36ff-4add-8200-b4bdae20e44d	\N	2026-01-26 17:16:52.459511		f		DIED	2020-01-26			2026-01-26 17:16:52.459511	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	6d57b583-b041-4b3a-9332-520aee8a6db8	7	4	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
6e534235-5f60-4969-8355-542f21663f33	\N	2026-01-26 17:17:41.116737		f		DIED	2020-01-26			2026-01-26 17:17:41.116737	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	8f3fc4d7-8ee1-46bb-9f3c-8eb0c65b22e3	7	280	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
9bd994ca-e311-4f24-9cd5-40bd8904cdca	\N	2026-01-26 17:18:14.686574		f		DIED	2020-01-26			2026-01-26 17:18:14.686574	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	52529e4f-a58c-4d1e-a31f-1668a83d20b3	7	39	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
1f6170fd-9155-4807-8eb5-688dbacb570a	\N	2026-01-26 17:18:57.936546		f		DIED	2020-01-26			2026-01-26 17:18:57.936546	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	36be09b0-41c8-4eeb-92f9-4f8b2755a7af	7	125	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
da126aac-fd74-4ff8-ba8b-06447a50c3c0	\N	2026-01-26 17:20:15.706748		f		DIED	2020-01-26			2026-01-26 17:20:15.706748	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	7af0668e-c5f6-4d03-be82-3fd102c31a87	7	327	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
e4785c1b-68e9-4c6c-bb1c-fa71ead12235	\N	2026-01-26 17:20:46.96666		f		DIED	2020-01-26			2026-01-26 17:20:46.96666	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	6a3d6233-cd7c-4938-9814-09b54673f6a2	7	19	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
1241a674-bef0-4f75-b762-e8b3045b3510	\N	2026-01-26 17:21:19.899037		f		DIED	2020-01-26			2026-01-26 17:21:19.899037	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	2e37d740-6955-4b8f-b3e5-ebddfaa7da2c	7	80	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
abdad23d-d3c9-4aa7-8f9c-abcb9ffa5a0a	\N	2026-01-26 17:21:55.678842		f		DIED	2020-01-26			2026-01-26 17:21:55.678842	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	77606368-aaaa-4d27-8245-3e50ea2b991a	7	108	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
44f8f1e5-c2f1-47b4-978d-53985a0a6452	\N	2026-01-26 17:22:23.582703		f		DIED	2020-07-26			2026-01-26 17:22:23.582703	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	408e8b14-cf9c-4f32-a9ef-b077eb733bc7	7	4	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
865a9a3a-b056-4e80-ada6-7eb2066c2fb4	\N	2026-01-26 17:24:00.80792		f		DIED	2020-01-26			2026-01-26 17:24:00.80792	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	248d7281-9cb8-425d-aa64-065312c6ec7a	7	6	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
775c5816-deca-4914-8167-f8a26f5429be	\N	2026-01-26 17:24:35.105457		f		DIED	2020-01-26			2026-01-26 17:24:35.105457	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	36be09b0-41c8-4eeb-92f9-4f8b2755a7af	7	2	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
c695b839-0a65-4179-8968-772637166ffc	\N	2026-01-26 17:38:44.965523		f		DIED	2020-01-26			2026-01-26 17:38:44.965523	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	5d111e98-c9b3-4c70-9bf7-4ee6eb1f07ac	7	258	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
119bbde9-6780-4155-b069-f3f7da92ccbf	\N	2026-01-26 17:39:23.484129		f		DIED	2020-01-26			2026-01-26 17:39:23.484129	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	03e86214-2262-42f8-af30-a95b93ec3953	7	404	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
e87ec402-b908-4042-b7f7-9ac3c2d97e6b	\N	2026-01-26 17:39:56.852891		f		DIED	2020-01-26			2026-01-26 17:39:56.852891	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	502fe166-d09d-4cd7-a112-f8a0d09ef0f9	7	423	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
14f9f452-2a87-4fc7-a608-930d490e5414	\N	2026-01-26 17:40:28.056127		f		DIED	2020-01-26			2026-01-26 17:40:28.056127	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	8a8afcf1-6a6c-469e-b204-a49856a94265	7	15	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
34fb9b55-784b-46b7-841e-f4db0086186b	\N	2026-01-26 17:41:06.987904		f		DIED	2020-01-26			2026-01-26 17:41:06.987904	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	b8e850a2-01b8-4043-8055-9e222b062c77	7	101	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
7a022ee7-298c-42ae-a3e3-60cb39a56d4e	\N	2026-01-26 17:41:37.230015		f		DIED	2020-01-26			2026-01-26 17:41:37.230015	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	fb3382b0-8644-4526-b1bb-8be28bd7ef31	7	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
2712c3a8-dc73-4001-b8ba-3fa5cc9076dc	\N	2026-01-26 17:42:07.862079		f		DIED	2020-01-26			2026-01-26 17:42:07.862079	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	0e68f9e3-04fa-477f-bb49-a4f3dbf7cdcd	7	175	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
d1fc7701-ab38-49f1-9c06-5aa9394e2a7d	\N	2026-01-26 17:42:38.034843		f		DIED	2020-10-26			2026-01-26 17:42:38.034843	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	3b8a7600-d8eb-46c9-8ed6-c2b7bc184a49	7	324	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
3cb5e27f-43e4-4beb-9278-8d11613b3cd6	\N	2026-01-26 17:43:56.589781		f		DIED	2020-01-26			2026-01-26 17:43:56.589781	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	24fa12c6-a0f6-4ae9-b543-486bf49457d8	7	15	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
968e8604-3377-45f8-85b0-2686c8d60668	\N	2026-01-26 17:44:26.077304		f		DIED	2020-01-26			2026-01-26 17:44:26.077304	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	c700f3d6-95ce-41f8-9704-700e0d15327d	7	105	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
66d8e991-f3a3-481e-8e6c-53453d0c9afc	\N	2026-01-26 17:44:52.468427		f		ONGOING	2020-01-26			2026-01-26 17:44:52.468427	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	d47b3856-d6ab-4719-a823-c5158e2e55b5	7	11	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
37753ef8-2445-48f2-b3b5-5d05d22891ed	\N	2026-01-26 17:45:33.741217		f		DIED	2020-01-26			2026-01-26 17:45:33.741217	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	a5890cc5-c2a5-401e-9cba-7187eaf352b8	7	78	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
b032dbcb-5326-4a0d-9c88-51611cfaf968	\N	2026-01-26 17:46:08.236437		f		DIED	2020-01-26			2026-01-26 17:46:08.236437	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	1fc47c21-a462-477d-a5e4-ebcbb8c82868	7	190	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
203835ff-62e5-4347-8137-205078e3ff15	\N	2026-01-26 17:46:52.623796		f		DIED	2020-01-26			2026-01-26 17:46:52.623796	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	8a8afcf1-6a6c-469e-b204-a49856a94265	7	3	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
d0423122-b5da-4ead-b930-6ab682f59a8b	\N	2026-01-26 17:47:23.403568		f		DIED	2020-01-26			2026-01-26 17:47:23.403568	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	24fa12c6-a0f6-4ae9-b543-486bf49457d8	7	4	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
a41aadf5-0b00-4d6a-8d4d-b2357dc01ee9	\N	2026-01-26 17:47:50.167953		f		DIED	2020-01-26			2026-01-26 17:47:50.167953	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	a5890cc5-c2a5-401e-9cba-7187eaf352b8	7	3	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
f8c9b559-05c7-46d8-95ba-c663600c2a59	\N	2026-01-26 18:12:30.505582		f		DIED	2020-01-26			2026-01-26 18:12:30.505582	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	74bc557d-5a0d-4256-ae50-a5f44d2f8642	7	183	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
d5d492a7-ec9a-4fbe-af79-b1dcd90c863a	\N	2026-01-26 18:13:06.26064		f		DIED	2020-01-26			2026-01-26 18:13:06.26064	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	9b0f94a1-e5f1-4e19-a4ca-f669feb572f4	7	6	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
107a4d31-ea6c-4202-84bb-2d27ecc253dc	\N	2026-01-26 18:13:38.595427		f		DIED	2020-01-26			2026-01-26 18:13:38.595427	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	d14c69d5-e42a-4814-a157-9a8c37888e87	7	31	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
8ae6c78d-2a04-400f-86c5-b1eb63d9dd10	\N	2026-01-26 18:14:03.199922		f		DIED	2020-01-26			2026-01-26 18:14:03.199922	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	ed405a85-b4da-4ae4-a8c7-4a400f21c156	7	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
c7522c62-dc0f-4ee1-86e1-a92ca32cb5fc	\N	2026-01-26 18:14:35.8923		f		DIED	2020-01-26			2026-01-26 18:14:35.8923	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	9aad0df3-5600-4f4f-ac19-de95496874cd	7	55	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
ce3e1b0a-cca7-414d-a514-81f9c77e4585	\N	2026-01-26 18:15:04.208682		f		DIED	2020-01-26			2026-01-26 18:15:04.208682	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	436116fc-c19e-4651-9acc-c1df63be7920	7	152	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
93c6795d-0928-4ade-89f6-baa3bd7b3843	\N	2026-01-26 18:15:32.120898		f		DIED	2020-01-26			2026-01-26 18:15:32.120898	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	eb66f276-ea23-4729-b9ea-66c5167c0524	7	20	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
eae05af1-492f-4085-85f1-3459bf762a69	\N	2026-01-26 18:15:59.392614		f		DIED	2020-01-26			2026-01-26 18:15:59.392614	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	f03cc71c-5372-40cc-a300-e67d48195fab	7	99	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
a8add247-b7e4-48f2-85ed-09919becf4c1	\N	2026-01-26 18:16:38.203236		f		DIED	2020-01-26			2026-01-26 18:16:38.203236	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	d14c69d5-e42a-4814-a157-9a8c37888e87	7	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
2c2dd76c-c962-4909-abdf-e9f2151cf5de	\N	2026-01-26 18:17:07.056696		f		DIED	2020-01-26			2026-01-26 18:17:07.056696	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	436116fc-c19e-4651-9acc-c1df63be7920	7	21	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
317891e2-d20b-4f69-b6af-7d06bf960ff2	\N	2026-01-26 18:35:13.052344		f		DIED	2020-01-26			2026-01-26 18:35:13.052344	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	80a79d80-cc0f-4c91-8faf-afb0ef358d31	9	10	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
ea3a420d-bdd6-4593-a63f-e6023ad1297c	\N	2026-01-26 18:35:37.979694		f		DIED	2020-01-26			2026-01-26 18:35:37.979694	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	270e4609-cfa4-4d4d-8c82-8d1a5485a701	9	2	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
ee9c865b-67a4-4a9c-99b1-796909ca022d	\N	2026-01-26 18:36:06.719006		f		DIED	2020-01-26			2026-01-26 18:36:06.720017	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	0afe5987-189f-4f70-a209-83c087fa125f	9	84	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
9925017d-8d2c-4611-bc6e-4c6c34b07f69	\N	2026-01-26 18:38:46.498147		f		DIED	2020-01-26			2026-01-26 18:38:46.498147	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	9031e672-c4d3-4a62-a159-39f574907f6c	9	4	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
2718df42-e405-44a6-93f8-5379d20e2a50	\N	2026-01-26 18:41:16.469772		f		ONGOING	2020-01-26			2026-01-26 18:41:16.469772	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	dffe29a4-7e7d-4dda-b6c6-794d38a8c790	9	5	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
0ef463b0-8201-4933-a709-5a3ad008562c	\N	2026-01-26 18:46:34.104501		f		ONGOING	2020-01-26			2026-01-26 18:46:34.104501	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	3ee5ee1f-5cce-4498-b5b0-6ff26d863bd1	9	13	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
0b6ebec5-2800-483c-b89e-591718e87e36	\N	2026-01-26 18:36:41.695568		f		DIED	2020-01-26			2026-01-26 18:36:41.695568	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	aff18dae-02e2-477e-90f6-ad7b1af5e8c8	9	4	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
08a3d5a8-3768-479d-adc7-17a86e251a33	\N	2026-01-26 18:37:20.078546		f		DIED	2020-01-26			2026-01-26 18:37:20.078546	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	eaa55aca-64ae-4159-a440-174368bf742e	9	40	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
2c5379cb-1a7f-44f3-9023-856237cca69b	\N	2026-01-26 18:37:46.230396		f		DIED	2020-01-26			2026-01-26 18:37:46.230396	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	2d6c32a2-2908-4fde-9dc5-1cadf85c8a69	9	43	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
28330ee4-2a6f-4c87-b4e3-99a4751d8ba9	\N	2026-01-26 18:38:16.28453		f		DIED	2020-01-26			2026-01-26 18:38:16.28453	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	7b5f80ff-8150-4599-b13f-3ee49d3e44c2	9	50	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
87936d1e-4595-4b0a-bc98-c9f15f198836	\N	2026-01-26 18:39:12.439418		f		DIED	2020-01-26			2026-01-26 18:39:12.439418	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	febab8fd-b06b-46ab-83f6-1ca5f908e1be	9	4	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
9bcae341-011f-4e4c-8a74-bc26bea5da23	\N	2026-01-26 18:39:40.285457		f		DIED	2020-01-26			2026-01-26 18:39:40.285457	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	2eacdee2-9783-471e-bde6-51cda0f0b5c4	9	40	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
47e57f61-ef31-45e1-bcca-289b8b67e7f5	\N	2026-01-26 18:40:20.557478		f		DIED	2020-01-26			2026-01-26 18:40:20.557478	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	7b5f80ff-8150-4599-b13f-3ee49d3e44c2	9	380	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
ae361573-e737-42d5-b137-07549c47ae63	\N	2026-01-26 18:41:36.915611		f		ONGOING	2020-01-26			2026-01-26 18:41:36.915611	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	5f2b4d04-108c-455b-a072-f38b5c7e8b15	9	9	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
f3b39228-dc15-48d4-a5ee-f0bbac10cc15	\N	2026-01-26 18:47:00.212961		f		DIED	2020-01-26			2026-01-26 18:47:00.212961	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	9a595b6a-26fe-44c7-86b4-aaa0e38be853	9	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
5e505d68-f328-4038-8ebf-7b2ad4195238	\N	2026-01-26 18:47:25.67645		f		ONGOING	2020-01-26			2026-01-26 18:47:25.67645	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	67ebc4ff-e780-4aae-aa4d-b8a3ca928456	9	298	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
227f00a8-1c3c-4cd5-b19b-de362b7f381f	\N	2026-01-26 18:47:52.109509		f		DIED	2020-01-26			2026-01-26 18:47:52.109509	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	c6a07e12-e5c7-4382-9f6a-ba3a2a7ce71a	9	10	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
18a3b67f-0fab-49f0-ba15-9febf40322b7	\N	2026-01-26 18:48:37.156947		f		DIED	2020-01-26			2026-01-26 18:48:37.156947	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	bc1bf4ef-2973-4110-9362-4d4e1033ae7d	9	620	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
51fffcc1-d9b2-41a2-bfbc-2abe3672143f	\N	2026-01-26 18:49:31.735784		f		ONGOING	2020-01-26			2026-01-26 18:49:31.735784	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	7377a7ac-b7ea-4cf3-8914-68abebcfad88	9	52	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
78621c77-e9c8-4030-90fc-45823f9f215c	\N	2026-01-26 19:31:56.965971		f		ONGOING	2020-01-26			2026-01-26 19:31:56.965971	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	f7cc17b9-2dd2-43ec-889a-fcc60eb07f4c	9	1937	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
d90ac20a-d899-4a61-b056-1330e09a4ac7	\N	2026-01-26 19:32:19.82797		f		DIED	2020-01-26			2026-01-26 19:32:19.82797	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	81fa5008-0d1f-47bc-b607-f0285228fcf0	9	13	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
182771b1-6c94-4401-bc63-548a6505b8c3	\N	2026-01-26 19:32:43.797418		f		DIED	2020-01-26			2026-01-26 19:32:43.797418	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	429bf0d3-1b8a-4e40-bae6-b527795f9021	9	30	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
0190ed9b-5db5-41ba-a20f-42d53ef709f7	\N	2026-01-26 19:33:11.1366		f		DIED	2020-01-26			2026-01-26 19:33:11.1366	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	21cd575c-2d39-47a3-84be-9cf764a5127c	9	38	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
36b14982-b766-4609-aaaf-915e6a9afdd4	\N	2026-01-26 19:33:44.969117		f		DIED	2020-01-26			2026-01-26 19:33:44.969117	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	8a30c051-299b-43b4-a599-0382a8aeca95	9	178	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
70a1ea8c-d661-4ebb-92f8-8eadfcbab962	\N	2026-01-26 19:34:15.78371		f		DIED	2020-01-26			2026-01-26 19:34:15.78371	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	66cc87a6-e7fa-4195-b1a2-e1ca55e904e8	9	137	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
245f9d9f-7e4b-4aa4-9bad-7284358ab7c4	\N	2026-01-26 19:34:47.152212		f		DIED	2020-01-26			2026-01-26 19:34:47.152212	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	31b3b356-4f6f-4deb-9369-8c08e3325ba9	9	23	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
730b8bff-144d-42b2-8029-1c23f7677849	\N	2026-01-26 19:36:18.11814		f		DIED	2020-01-26			2026-01-26 19:36:18.11814	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	5f9f46aa-ce8b-4997-9dfb-9607abef2e7b	9	6	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
f999c25f-12e6-44ab-a49f-27b462dd72fe	\N	2026-01-26 19:36:50.129157		f		DIED	2020-01-26			2026-01-26 19:36:50.129157	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	9be18bdc-263b-4ffd-b3fe-4ce0f4b73150	9	17	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
22ddee2f-e31b-4dbf-b645-8ff5f056eb2d	\N	2026-01-26 19:37:12.54194		f		ONGOING	2020-01-26			2026-01-26 19:37:12.54194	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	b17cc979-4219-40a8-a253-e088b47fbfe6	9	284	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
7bf62e8c-eb21-415b-85fb-03ba6bac6f69	\N	2026-01-26 19:37:55.30852		f		ONGOING	2020-01-26			2026-01-26 19:37:55.30852	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	f7cc17b9-2dd2-43ec-889a-fcc60eb07f4c	9	260	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
5acc08f5-95df-4b29-9366-ac7d5f3caf91	\N	2026-01-26 19:38:18.315976		f		ONGOING	2020-01-26			2026-01-26 19:38:18.315976	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	eb799bac-acc4-49b4-a736-744f1b46c81f	9	105	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
e2ffa3fd-6d07-4d1e-a10e-f01b87b9b908	\N	2026-01-26 19:39:30.124795		f		ONGOING	2020-01-26			2026-01-26 19:39:30.124795	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	21cd575c-2d39-47a3-84be-9cf764a5127c	9	2324	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
193f05f3-6c91-4c56-802b-89c491e9da6b	\N	2026-01-26 19:43:40.621224		f		DIED	2020-01-26			2026-01-26 19:43:40.621224	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	595d8e26-4956-45e7-9ea5-dc135bd7d674	5	40	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
a811e2f2-0dcc-4910-87ad-604efe9099e3	\N	2026-01-26 19:44:04.953986		f		DIED	2020-01-26			2026-01-26 19:44:04.953986	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	de1d801a-9e72-402c-b6c7-6433eb943e16	5	10	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
edd21081-d105-4abf-8710-0babcc373c99	\N	2026-01-26 19:44:27.051044		f		DIED	2020-01-26			2026-01-26 19:44:27.051044	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	4518a71e-9aae-4d59-bebf-8c874313faf3	5	15	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
781bb6ac-7d13-47f0-852a-f3f683786b0b	\N	2026-01-26 19:44:54.533958		f		DIED	2020-01-26			2026-01-26 19:44:54.533958	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	29255e56-38a1-4cbb-9684-af70e2d58dfc	5	105	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
c07a4491-d838-4d59-8dfc-3391e16dc797	\N	2026-01-26 19:45:43.363764		f		DIED	2020-01-26			2026-01-26 19:45:43.363764	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	595d8e26-4956-45e7-9ea5-dc135bd7d674	5	6	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
c4860a51-d8d9-41c3-b788-2312cae35a49	\N	2026-01-26 19:51:03.487584		f		DIED	2020-01-26			2026-01-26 19:51:03.487584	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	258d33f5-f348-4479-9e8d-feacdeb65f80	5	172	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
aa0aeb6a-5631-429e-ab2e-ac6d3488ba6a	\N	2026-01-26 19:51:28.07395		f		DIED	2020-01-26			2026-01-26 19:51:28.07395	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	937ff8ec-4520-4593-9bd2-0bda3902c29e	5	5	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
774331a3-e555-423d-8278-ccdfcf18ab11	\N	2026-01-26 19:51:57.618565		f		DIED	2020-01-26			2026-01-26 19:51:57.618565	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	ac26e74d-0eae-4b25-9c8b-c77a8e7c6929	5	30	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
fe557d0b-c0e3-4e2e-a874-57da96e8d5b9	\N	2026-01-26 19:52:19.609932		f		DIED	2020-01-26			2026-01-26 19:52:19.609932	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	f5fd144d-192d-4c13-97d6-9f7b5215ae02	5	3	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
66fb9e73-c844-47d7-a717-7658d78230cb	\N	2026-01-26 21:09:38.71585		f		DIED	2020-01-26			2026-01-26 21:09:38.71585	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	526211c9-cb82-47a4-8fa0-fd15c126f03f	5	103	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
544b6c87-fbde-4a91-881c-2671534fca9d	\N	2026-01-26 21:10:24.435576		f		DIED	2020-01-26			2026-01-26 21:10:24.435576	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	808bc30c-2f12-4779-8817-cc1313a3fd0c	5	1549	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
64d33264-f9f3-44f9-ad26-d8a52a0b6513	\N	2026-01-26 21:11:02.978146		f		DIED	2020-01-26			2026-01-26 21:11:02.978146	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	1aff661a-2868-4608-a0de-c9a0930b2bc1	5	10	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
d5ad9d6c-b517-482f-8d6c-e693d7489a24	\N	2026-01-26 21:15:12.671265		f		DIED	2020-01-26			2026-01-26 21:15:12.671265	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	81ac85a3-30bf-435b-ad7b-68c115be52b9	5	110	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
f526742a-ba63-4227-affa-2ffe9b8b3d23	\N	2026-01-26 21:15:41.936284		f		DIED	2020-01-26			2026-01-26 21:15:41.936284	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	bba20175-5ca7-4979-8c61-cc1596ca6401	5	23	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
a0798d4b-f6b1-408c-90e3-b1c95ad085a2	\N	2026-01-26 21:16:12.235608		f		DIED	2020-01-26			2026-01-26 21:16:12.235608	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	31d30778-3b8e-4d74-908c-975355497c09	5	3	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
53df3dba-5150-4def-87d3-eafa55ed8cb0	\N	2026-01-26 21:16:40.016734		f		DIED	2020-01-26			2026-01-26 21:16:40.016734	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	bba20175-5ca7-4979-8c61-cc1596ca6401	5	12	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
a241c22f-9204-4149-b243-2ae00afa52d0	\N	2026-01-26 21:33:49.809211		f		DIED	2020-01-26			2026-01-26 21:33:49.809211	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	14478c31-6429-46b5-a621-8f32ef65f14e	7	22	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
fc89a31a-bf41-4679-9444-d5996cac3abd	\N	2026-01-26 21:34:26.679411		f		DIED	2020-01-26			2026-01-26 21:34:26.679411	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	32008ee1-8a15-4ef1-9016-f011c44d100a	7	4	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
92f8e8a4-aaab-48ea-ae36-d940bfd00d58	\N	2026-01-26 21:35:01.457778		f		ONGOING	2020-01-26			2026-01-26 21:35:01.457778	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	1024b8d1-2902-4a32-a54e-6753db32267f	7	14	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
d20d24a0-7ee9-4022-b72e-ccfb8830ef8a	\N	2026-01-26 21:35:27.838168		f		DIED	2020-01-26			2026-01-26 21:35:27.838168	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	74f03f06-d787-4d6b-833c-ae0f9f9be537	7	5	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
46ecfb4d-216d-4530-885c-ec00157bcc54	\N	2026-01-26 21:35:57.419666		f		DIED	2020-01-26			2026-01-26 21:35:57.419666	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	115fb6c6-8694-44c9-819f-68a66fbbfb61	7	836	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
771978a9-7cd2-4333-9527-2a4052260ca3	\N	2026-01-26 21:36:23.021705		f		ONGOING	2020-01-26			2026-01-26 21:36:23.021705	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	2656606a-bca0-4fff-a250-d5f3fb8cbaae	7	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
16c88128-85cc-4d50-9da2-b7ae48d66311	\N	2026-01-26 21:37:18.569676		f		ONGOING	2020-01-26			2026-01-26 21:37:18.569676	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	05b8eec4-8f03-458f-b3af-0271d786e08d	7	4	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
0141c24f-136e-4420-8c8f-cb7a82d9022c	\N	2026-01-26 21:38:01.399281		f		DIED	2020-01-26			2026-01-26 21:38:01.399281	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	74f03f06-d787-4d6b-833c-ae0f9f9be537	7	13	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
0bed1784-d418-4515-abda-053a3f58cdb6	\N	2026-01-26 21:38:27.784165		f		DIED	2020-01-26			2026-01-26 21:38:27.784165	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	115fb6c6-8694-44c9-819f-68a66fbbfb61	7	71	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
3dbdea30-e1bd-4f85-b58f-f194fdd75fa9	\N	2026-01-26 21:50:05.539823		f		DIED	2020-01-26			2026-01-26 21:50:05.539823	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	89006adb-0a53-4aa6-a956-7e8b6b804281	7	4	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
62f11a70-b618-48f0-9fd8-cc94e34496cd	\N	2026-01-26 21:50:32.764238		f		ONGOING	2020-01-26			2026-01-26 21:50:32.764238	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	566f402d-ef2d-44e6-b319-dc32a40f7703	7	26	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
3bbae8ca-b125-4590-b51b-ed03aaf38c00	\N	2026-01-26 21:50:55.73246		f		ONGOING	2020-01-26			2026-01-26 21:50:55.73246	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	996e5154-2247-45cc-9113-4f9dd5f378a5	7	114	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
8256c9f6-c8ca-4d6f-9876-490a80274c3f	\N	2026-01-26 21:51:21.707649		f		DIED	2020-01-26			2026-01-26 21:51:21.707649	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	2ce70e93-cf96-4219-9408-b17b40e78252	7	40	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
b856cd5a-fa33-45f7-b5ac-27fc6cf0c663	\N	2026-01-26 21:52:26.669381		f		DIED	2020-01-26			2026-01-26 21:52:26.669381	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	a08046d3-b77e-4408-9562-161c586b8559	7	20	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
b6db361e-4ad5-4139-937f-b2f4996cd74e	\N	2026-01-26 21:53:05.336894		f		DIED	2020-01-26			2026-01-26 21:53:05.336894	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	fec1a952-c810-4b0f-aa73-4c0199b6931a	7	10	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
5244acf0-1d82-4b32-8180-c6435c91d325	\N	2026-01-26 21:51:53.19328		f		ONGOING	2020-01-26			2026-01-26 21:51:53.19328	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	fec1a952-c810-4b0f-aa73-4c0199b6931a	7	6	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
6ae302a9-b51c-4258-8877-3d43a73b256e	\N	2026-01-26 22:17:37.755605		f		DIED	2020-01-26			2026-01-26 22:17:37.755605	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	a7b2bff8-f3af-44c1-b94f-4f7fee43df02	9	5	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
46c9897d-d42c-4914-9d7b-be574d3f7bd1	\N	2026-01-26 22:18:24.163414		f		DIED	2020-01-26			2026-01-26 22:18:24.163414	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	726cbe0a-861d-4709-b536-fdbc084c2f71	9	23	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
bcebc147-7d93-4900-ad40-7502b9a9439b	\N	2026-01-26 22:19:01.208717		f		ONGOING	2020-01-26			2026-01-26 22:19:01.208717	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	e516899b-e8bc-4cec-bf1e-650fa2f94dfd	9	20	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
8ef8e4d7-d2ad-43e7-8145-e14c950d92e4	\N	2026-01-26 22:19:33.394311		f		ONGOING	2020-01-26			2026-01-26 22:19:33.394311	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	726cbe0a-861d-4709-b536-fdbc084c2f71	9	20	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
06e631c2-16c6-40e6-9e4f-d06345101b63	\N	2026-01-26 22:20:07.39344		f		ONGOING	2020-01-26			2026-01-26 22:20:07.39344	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	98f384bf-dc94-411c-b7ca-0aefd0e5186c	9	200	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
83c1d7e8-25b6-47c2-9df6-7fcbc735f5ff	\N	2026-01-26 22:20:43.164353		f		ONGOING	2020-01-26			2026-01-26 22:20:43.164353	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	e516899b-e8bc-4cec-bf1e-650fa2f94dfd	9	9	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
d6d0959e-71ca-4159-906a-5e88a1531d21	\N	2026-01-26 22:36:46.922308		f		DIED	2020-01-26			2026-01-26 22:36:46.922308	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	741c2838-437a-4715-a451-24975abffaf2	9	27	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
8d6dda9a-c30c-4c93-afcd-f11db1922778	\N	2026-01-26 22:37:11.840435		f		ONGOING	2020-01-26			2026-01-26 22:37:11.840435	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	43d34dc2-0894-4eb8-9ab3-bf49c5a212de	9	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
b95bf4fa-35e2-4b08-b2ad-1882f6457839	\N	2026-01-26 22:37:44.04958		f		DIED	2020-01-26			2026-01-26 22:37:44.04958	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	83cd4d4b-c1d3-405a-b8c9-582f57b6d451	9	8	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
08fe8ce3-0e5d-4cb0-a621-d266eb42ddda	\N	2026-01-26 22:38:13.922454		f		ONGOING	2020-01-26			2026-01-26 22:38:13.922454	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	e51c1ae5-d16f-4d18-b1e8-9dff64a84438	9	28	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
fea1cd71-0e9f-4f52-9efb-8577de2b13bf	\N	2026-01-26 22:38:44.826152		f		ONGOING	2020-01-26			2026-01-26 22:38:44.826152	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	83174eef-4ccb-43be-8b03-acd8809503e7	9	20	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
845c2b20-aa77-44ef-8da9-d38a09788a0a	\N	2026-01-26 22:39:49.861233		f		DIED	2020-01-26			2026-01-26 22:39:49.861233	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	ead95db6-2ea2-4c5f-aa6d-bfadc2a7eb43	9	2	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
0f17daa3-a9d8-4f11-b486-1ad299302614	\N	2026-01-26 22:40:26.475915		f		ONGOING	2020-01-26			2026-01-26 22:40:26.475915	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	43d34dc2-0894-4eb8-9ab3-bf49c5a212de	9	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
18285b34-aeb3-46a6-8a16-1699e8cc8e8f	\N	2026-01-26 22:42:23.1348		f		ONGOING	2020-01-26			2026-01-26 22:42:23.1348	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	83174eef-4ccb-43be-8b03-acd8809503e7	9	290	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
5db817a8-390b-45e0-a5fe-81c241c830be	\N	2026-01-26 23:04:49.958209		f		DIED	2020-01-26			2026-01-26 23:04:49.958209	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	741c2838-437a-4715-a451-24975abffaf2	9	27	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
5d7b8a69-e690-47a9-b5fa-3811993cd474	\N	2026-01-26 23:05:22.689283		f		DIED	2020-01-26			2026-01-26 23:05:22.689283	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	83cd4d4b-c1d3-405a-b8c9-582f57b6d451	9	8	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
ffe3219b-559c-4f9a-8ac3-12acb28632c1	\N	2026-01-26 23:06:09.047235		f		ONGOING	2020-01-26			2026-01-26 23:06:09.047235	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	e51c1ae5-d16f-4d18-b1e8-9dff64a84438	9	28	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
c5a9e9df-e039-4acc-b5b0-2bb57b50d906	\N	2026-01-26 23:06:39.30332		f		DIED	2020-01-26			2026-01-26 23:06:39.30332	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	ead95db6-2ea2-4c5f-aa6d-bfadc2a7eb43	9	2	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
8296b788-5952-4f3e-8527-0f94dbb58167	\N	2026-01-26 23:07:40.072548		f		ONGOING	2020-01-26			2026-01-26 23:07:40.072548	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	43d34dc2-0894-4eb8-9ab3-bf49c5a212de	9	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
b32e82ae-98de-4baa-98a5-bde5b6bce437	\N	2026-01-26 23:08:22.391399		f		ONGOING	2020-01-26			2026-01-26 23:08:22.391399	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	83174eef-4ccb-43be-8b03-acd8809503e7	9	290	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
f2f9bf7e-8adb-48fd-a4f9-48e7f0319a1b	\N	2026-01-26 23:09:44.709275		f		ONGOING	2020-01-26			2026-01-26 23:09:44.709275	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	cfcf37ef-1902-485a-8e27-dd297660d37b	5	3	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
094b2ea3-b1c9-4223-b782-f4618e754088	\N	2026-01-26 23:11:00.474017		f		DIED	2020-01-26			2026-01-26 23:11:00.474017	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	c007a0e1-a14c-4cfb-bdca-616c3fcf8bbd	5	21	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
dfc79fab-44b9-4846-9839-2846bb550a80	\N	2026-01-26 23:11:29.543517		f		DIED	2020-01-26			2026-01-26 23:11:29.543517	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	28520c25-50a5-424a-94a2-21afe5456b8d	5	5	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
f3bcc97f-5710-429e-9a9c-0bdc9892d560	\N	2026-01-26 23:11:56.399419		f		ONGOING	2020-01-26			2026-01-26 23:11:56.399419	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	8d563637-f368-49c4-a851-f7ec5e9116ab	5	20	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
98ef496c-d9fe-4515-a0c2-5b6254fa65f2	\N	2026-01-26 23:12:21.132407		f		ONGOING	2020-01-26			2026-01-26 23:12:21.132407	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	9aada6e2-cb21-463c-964d-30d60dcd996d	5	8	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
80a994e4-e363-413b-927d-afc8046a5497	\N	2026-01-26 23:13:03.602564		f		DIED	2020-01-26			2026-01-26 23:13:03.602564	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	e1a48126-7589-45df-8c56-b1b21bf58e4c	5	3	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
faf92631-27cd-4f86-9cb8-fae04ca36708	\N	2026-01-26 23:28:55.36672		f		ONGOING	2020-01-26			2026-01-26 23:28:55.36672	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	9e50cfae-c5e1-4321-9814-ff7705ca6165	5	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
bb448a38-18d5-4063-a12b-b1547d001878	\N	2026-01-26 23:29:39.229676		f		DIED	2020-01-26			2026-01-26 23:29:39.229676	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	3430251e-238d-4782-b060-fad61b54fe92	5	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
269e8a14-f874-4375-9428-0ac6292b99b5	\N	2026-01-27 08:58:50.590548		f		DIED	2020-01-27			2026-01-27 08:58:50.590548	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	3430251e-238d-4782-b060-fad61b54fe92	5	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
b8a3fc57-5832-4b09-b07f-fca2ca81a928	\N	2026-01-27 09:08:40.252217		f		DIED	2020-01-27			2026-01-27 09:08:40.252217	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	cc59bdcf-358e-487c-a688-4a3e3aad1ba8	7	7	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
c395e835-248c-4303-a7f2-4f59199d81e8	\N	2026-01-27 09:09:07.969504		f		DIED	2020-01-27			2026-01-27 09:09:07.969504	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	7e370397-7ee8-41e1-a958-73d80a9516c3	7	8	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
5d266f36-bc0d-4974-abb9-019f1a3670c3	\N	2026-01-27 09:09:40.427363		f		DIED	2020-01-27			2026-01-27 09:09:40.427363	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	4ca2cf28-b00c-41e5-8995-6b8fa11dc7e9	7	77	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
babd3de8-1a77-428c-b4ff-b5ce7626500a	\N	2026-01-27 09:10:08.269607		f		ONGOING	2020-01-27			2026-01-27 09:10:08.269607	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	20a6093b-2a69-4b4c-96fb-a8725fb110cc	7	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
cadfecce-24f5-459d-93ba-95886eec50aa	\N	2026-01-27 09:10:34.991387		f		ONGOING	2020-01-27			2026-01-27 09:10:34.991387	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	e4b1719b-17f5-4f5c-aee1-f1dad458a73e	7	10	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
5f48f34b-9acf-444e-87ce-228e5a1a4e2e	\N	2026-01-27 09:22:40.510805		f		ONGOING	2020-01-27			2026-01-27 09:22:40.510805	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	285bdc01-f1c3-4927-b591-6b90728d76b9	7	41	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
0cba9ae4-1c4d-4007-be3d-2ea30e6c9c73	\N	2026-01-27 09:23:15.84639		f		DIED	2020-01-27			2026-01-27 09:23:15.84639	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	37c01067-97d2-405f-84a9-cf50de299aec	7	5	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
e4b54458-0740-4156-b06f-de55c1394968	\N	2026-01-27 09:23:50.283684		f		DIED	2020-01-27			2026-01-27 09:23:50.283684	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	2f084dfc-6285-4246-a08e-f5bd7665c2a6	7	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
256ae51e-7ab0-4028-9fc7-c2f6f52a3101	\N	2026-01-27 09:24:25.69392		f		DIED	2020-01-27			2026-01-27 09:24:25.69392	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	37c01067-97d2-405f-84a9-cf50de299aec	7	5	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
beac4886-4ea5-4270-b8d3-5cf7490cad62	\N	2026-01-27 09:24:54.049573		f		ONGOING	2020-01-27			2026-01-27 09:24:54.049573	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	7b8caaf3-7e5b-4b59-9a4e-8efffaafb5ac	7	4	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
efcf5867-a7ac-4dd0-bb92-c614060b9c46	\N	2026-01-27 09:34:56.357517		f		DIED	2020-01-27			2026-01-27 09:34:56.357517	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	6a7ee9fe-8a4b-4971-9604-c6d448170d0b	7	2	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
cf19a869-e433-49af-91b7-339b0a41c49b	\N	2026-01-27 09:35:22.148881		f		DIED	2020-01-27			2026-01-27 09:35:22.148881	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	f81d04d6-8c05-4f73-8641-ca398615d2f6	7	5	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
3531ddb9-e71d-4fbe-939b-bb8c57cc3a99	\N	2026-01-27 09:35:43.301197		f		DIED	2020-01-27			2026-01-27 09:35:43.301197	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	98c1b464-e1b5-40d1-b3e4-fdc95771d2fe	7	8	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
879096da-2b70-4651-9503-c018f2145df6	\N	2026-01-27 09:36:09.280339		f		ONGOING	2020-01-27			2026-01-27 09:36:09.280339	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	8f6aeebb-67a0-484e-a643-71dc89a5f9b7	7	120	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
60b7af48-a7bf-4139-834f-d12e42f55158	\N	2026-01-27 09:36:45.749866		f		DIED	2020-01-27			2026-01-27 09:36:45.749866	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	98c1b464-e1b5-40d1-b3e4-fdc95771d2fe	7	2	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
eb4eae56-60f3-47df-807d-f2cb8fae2b68	\N	2026-01-27 09:37:16.475333		f		ONGOING	2020-01-27			2026-01-27 09:37:16.475333	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	8f6aeebb-67a0-484e-a643-71dc89a5f9b7	7	4	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
2be7c645-696d-4e6d-8d0c-5f60e776fa07	\N	2026-01-27 09:37:58.46241		f		DIED	2020-01-27			2026-01-27 09:37:58.46241	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	ebe20932-f21a-402c-85f3-a86b8bb52ef2	7	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
165be439-b32d-430d-8bc4-5afef5e3e79f	\N	2026-01-27 09:38:29.320444		f		DIED	2020-01-27			2026-01-27 09:38:29.320444	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	038ecca3-1d55-4e68-9054-78b3950fc8bb	7	37	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
7c03c13c-527c-4cfb-88bd-2622de76aa75	\N	2026-01-27 09:44:51.323039		f		ONGOING	2020-01-27			2026-01-27 09:44:51.323039	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	e8d6cd54-6526-44da-9b37-6b12cf9e0c98	5	25	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
20cba8e2-87a0-48f7-a764-1a253bbd1283	\N	2026-01-27 09:45:17.427785		f		ONGOING	2020-01-27			2026-01-27 09:45:17.427785	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	c3c56cc8-2307-4f7e-841c-ebb8f09c114c	5	3	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
af957bf6-f10c-442f-98ce-099425d564f2	\N	2026-01-27 09:45:40.908662		f		DIED	2020-01-27			2026-01-27 09:45:40.908662	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	29529a6d-d69f-4dba-8568-655e1d1b35c8	5	3	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
afcffcc2-955e-462d-b897-1d5b449ca32f	\N	2026-01-27 09:46:15.604796		f		DIED	2020-01-27			2026-01-27 09:46:15.604796	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	6e5e7cf6-71a2-4078-968e-93fb1b40aa9c	5	10	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
f16c2a6d-e566-4b7f-832b-2e37ac8bcf52	\N	2026-01-27 09:47:08.516468		f		DIED	2020-01-27			2026-01-27 09:47:08.516468	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	4fa87a14-3741-459c-b020-25c89425838a	5	53	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
7e14ea91-2da0-4383-993c-596b729c8880	\N	2026-01-27 09:47:40.146367		f		DIED	2020-01-27			2026-01-27 09:47:40.146367	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	c3c56cc8-2307-4f7e-841c-ebb8f09c114c	5	98	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
ab6baa5a-edf4-4c5e-a00d-c88f8120c90f	\N	2026-01-27 09:56:39.65639		f		DIED	2020-01-27			2026-01-27 09:56:39.65639	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	7256355d-3afb-4677-b680-0237114987c9	5	115	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
3e2914b0-73f0-4c3a-9d97-59b1f26dd4aa	\N	2026-01-27 09:57:02.72115		f		DIED	2020-01-27			2026-01-27 09:57:02.72115	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	0858eb59-41d7-4828-8174-7709b36a91fb	5	4	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
18acc1de-3e77-462e-a549-0a6c4d46ab50	\N	2026-01-27 09:57:25.592794		f		DIED	2020-01-27			2026-01-27 09:57:25.592794	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	c5a72793-9023-4d29-8a9a-56a37b3412da	5	5	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
7b5dd2bb-60a5-487b-b02b-137eac355612	\N	2026-01-27 09:57:59.946743		f		DIED	2020-01-27			2026-01-27 09:57:59.946743	\N	c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	5e2c12f0-e141-444e-8365-763135335af7	5	9	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
dd3fc633-1d1d-4cc1-be80-f480bcc1ec00	\N	2026-01-27 10:06:14.85241		f		DIED	2020-01-27			2026-01-27 10:06:14.85241	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	870f7938-0606-4736-a7d0-f2b2768e12cf	9	1	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
a2507e94-1817-4d4e-ad97-dddd7425c147	\N	2026-01-27 10:06:38.058007		f		DIED	2020-01-27			2026-01-27 10:06:38.058007	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	5edfd9dd-38de-468e-8ef1-d3f1041a86dc	9	3	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
45af8929-3a51-414a-8d6c-42ca9757ebfc	\N	2026-01-27 10:07:08.340235		f		DIED	2020-01-27			2026-01-27 10:40:14.698196	\N	5b01cd9b-4d32-4c11-95af-f59b5468ee20	759119d9-90c6-4b2d-9c4a-c5cd81b5f41f	9	1	disease-images/41b366d9-90c1-4df7-a74f-826f589ce7a0.jpg	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
9e68a65b-dba6-452d-a75e-f1b4a80ffc45	\N	2026-01-27 11:07:37.130168		f		ONGOING	2020-01-27			2026-01-27 11:07:37.130168	\N	6f5ff598-88a7-4d69-b3b6-0c26e78fd79e	e8d6cd54-6526-44da-9b37-6b12cf9e0c98	5	5	\N	e72c0140-5e78-4e29-9292-52406f7c300b	\N	\N	\N	\N
\.


--
-- Data for Name: diseases; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.diseases (id, affected_animal_types, created_at, description, disease_code, disease_name, is_active, is_notifiable, severity, updated_at, created_by, updated_by, animal_type_id, symptoms, treatment, created_by_vet) FROM stdin;
bcc0d295-8753-4703-9597-64c62c5ba826	\N	2026-01-22 13:26:10.124013	Bovine tuberculosis is an infectious disease of cattle. It is caused by the bacterium Mycobacterium bovis	BT-001	Bovine Tuberculosis	t	f	HIGH	2026-01-22 13:26:10.124013	\N	\N	\N			f
cf3804ee-9bf0-4a7f-b823-497947333e55	\N	2026-01-22 13:04:39.713431	Foot and mouth disease (FMD) is a severe, highly contagious viral disease of livestock that has a significant economic impact.	FMD-001	Foot and Mouth Disease (FMD)	t	t	HIGH	2026-01-22 13:23:57.530485	\N	\N	1c394491-78dc-46e8-bcfc-150ab9b921c2			f
fdf3e6af-b7fc-4911-8de3-2383a965b8b1	\N	2026-01-22 13:08:06.413132	A severe viral infection in cattle and buffalo, causing fever, painful skin nodules, swollen lymph nodes, and potential death	LSD-001	Lumpy Skin Disease (LSD)	t	t	MEDIUM	2026-01-22 13:24:08.270797	\N	\N	1c394491-78dc-46e8-bcfc-150ab9b921c2			f
8bdc507b-a611-4d97-ac06-60f75634994d	\N	2026-01-22 13:27:31.998732	Highly fatal bacterial disease, primarily affecting cattle and water buffalo, caused by specific Pasteurella multocida types	HS-001	Hemorrhagic Septicemia	t	t	MEDIUM	2026-01-22 13:27:31.998732	\N	\N	\N			f
58ec9ea5-b765-4da0-bf67-2e7ce0705419	\N	2026-01-22 13:28:22.817693	A highly fatal, acute bacterial disease in cattle, sheep, and goats	BQ-001	Black Quarter (Blackleg)	t	t	HIGH	2026-01-22 13:28:22.817693	\N	\N	\N			f
245918b9-30fb-4a0a-9c95-d6b2cb6e2858	\N	2026-01-22 13:31:14.266483	A highly contagious respiratory disease in cattle, caused by Bovine Herpesvirus type 1 (BHV-1), also known as Red Nose, characterized by fever, nasal/ocular discharge, conjunctivitis, and coughing, but can also manifest as reproductive issues	IBR-001	Infectious Bovine Rhinotracheitis (IBR)	t	t	HIGH	2026-01-22 13:31:14.266483	\N	\N	\N			f
2d9736c2-035e-4fac-8229-9aff2fc1703d	\N	2026-01-22 13:30:19.209492	A widespread, costly cattle disease caused by the Bovine Viral Diarrhoea Virus	BVD-001	Bovine Viral Diarrhoea (BVD)	t	f	MEDIUM	2026-01-22 13:31:34.477101	\N	\N	\N			f
672a57ab-0a53-40c8-a8a8-3c7020fec0d5	\N	2026-01-22 13:32:16.360393	A contagious, chronic, and often fatal bacterial infection of the intestinal tract in ruminants	JD-001	Johneâ€™s Disease (Paratuberculosis)	t	f	MEDIUM	2026-01-22 13:32:16.360393	\N	\N	\N			f
2578aef4-45e4-4e92-924c-9499284598b8	\N	2026-01-22 13:33:56.016868	Mastitis is swelling and redness, called inflammation, of breast tissue. It sometimes involves an infection	MS-001	Mastitis	t	t	MEDIUM	2026-01-22 13:33:56.016868	\N	\N	\N			f
199ee0a5-ba6f-4eb1-ad4f-c3b83d772ca5	\N	2026-01-22 13:34:28.879187	Trypanosomiasis is a parasitic disease, primarily known as African sleeping sickness, caused by Trypanosoma parasites, transmitted by the bite of infected tsetse flies in sub-Saharan Africa.	TP-001	Trypanosomiasis	t	t	MEDIUM	2026-01-22 13:34:28.879187	\N	\N	\N			f
5cf7fe1a-052d-4617-9065-438c3433d64b	\N	2026-01-22 13:35:07.847486	A tick-borne parasitic disease that infects red blood cells, causing flu-like symptoms (fever, chills, fatigue) or no symptoms at all	BE-001	Babesiosis	t	t	MEDIUM	2026-01-22 13:35:07.847486	\N	\N	\N			f
06b23a37-20da-4d2e-8f7e-90c3c52ba852	\N	2026-01-22 13:36:00.541143	Theileriae are obligate intracellular protozoan parasites that infect both wild and domestic Bovidae throughout much of the world.	TE-001	Theileriosis	t	t	LOW	2026-01-22 13:36:00.541143	\N	\N	\N			f
c338f40a-51e0-471f-8bc2-2f8ae29504e3	\N	2026-01-22 13:36:36.055043	Anaplasmosis is a bacterial infection spread by infected blacklegged or western blacklegged ticks	AP-001	Anaplasmosis	t	t	MEDIUM	2026-01-22 13:36:36.055043	\N	\N	\N			f
b5b40acc-e40e-48fd-b8dd-9b7b32090f1f	\N	2026-01-22 13:37:14.334845	Sheeppox and goatpox are serious, often fatal, diseases characterized by widespread skin eruption	SP-001	Sheep Pox	t	t	MEDIUM	2026-01-22 13:37:14.334845	\N	\N	\N			f
eb0a8567-81ca-41c8-bbfe-f574d73e3f45	\N	2026-01-22 13:37:56.334416	Goat Pox is\na highly contagious viral disease of goats and sheep	GP-001	Goat Pox	t	t	MEDIUM	2026-01-22 13:37:56.334416	\N	\N	\N			f
85bd2b18-3be5-4746-b953-cd31214b7474	\N	2026-01-22 13:29:32.213593	A severe, insect-borne viral disease affecting wild and domestic ruminants like sheep, goats, cattle, and deer, spread by Culicoides midges, but it doesn't affect humans	BTE-001	Bluetongue	t	t	MEDIUM	2026-01-22 13:38:37.472087	\N	\N	\N			f
75631f6d-7d03-42c9-b254-a3e6ccdf7317	\N	2026-01-22 14:00:22.854534	Salmonellosis is a symptomatic infection caused by bacteria of the Salmonella type	SA-001	Salmonellosis	t	t	MEDIUM	2026-01-22 14:00:22.854534	\N	\N	\N			f
8f5cd124-ee71-4092-bb51-fea523ceaf21	\N	2026-01-22 13:25:16.939674	Brucellosis is an infection caused by bacteria	BC-001	Brucellosis	t	f	MEDIUM	2026-01-22 13:59:49.53311	\N	\N	\N			f
6545334d-7c26-4d3e-a99e-962309ab3d4d	\N	2026-01-22 13:41:27.822109	Enterotoxaemia (or enterotoxemia), also known as pulpy kidney diseas	ET-001	Enterotoxaemia	t	t	HIGH	2026-01-22 13:41:27.822109	\N	\N	\N			f
9b4451f3-bce2-4682-a970-770d7c467724	\N	2026-01-22 13:42:37.806646	A highly contagious and deadly respiratory disease of goats and some wild ruminants	CCPP-001	Contagious Caprine Pleuropneumonia (CCPP)	t	t	HIGH	2026-01-22 13:42:37.806646	\N	\N	\N			f
ac25085a-4e96-400e-b45a-5a0b6cfe3ca5	\N	2026-01-22 13:43:24.843617	A contagious bacterial disease in sheep and goats (and sometimes other animals/humans)	CL-001	Caseous Lymphadenitis	t	t	MEDIUM	2026-01-22 13:43:24.843617	\N	\N	\N			f
8f17b7bb-9525-4ab6-9049-e6d034ec8c04	\N	2026-01-22 13:44:37.267541	a contagious, chronic bacterial infection of the intestine in ruminants (cattle, sheep, goats, etc.)	JD-002	Johneâ€™s Disease	t	f	MEDIUM	2026-01-22 13:44:37.267541	\N	\N	\N			f
9078897e-aa84-4d26-bf24-effcad6f2d3b	\N	2026-01-22 13:45:30.694848	A highly contagious viral skin disease in sheep and goats, causing painful blisters and scabs, often around the mouth but also on feet (strawberry footrot) and teats, leading to lameness and poor feeding	FR-001	Foot Rot Orf (Contagious Ecthyma)	t	t	HIGH	2026-01-22 13:45:30.694848	\N	\N	\N			f
340a0cd0-28b9-4e2a-b30e-5b7f56e51f48	\N	2026-01-22 13:47:28.996332	a bacterial infection, usually from Pasteurella multocida, common in animals but also zoonotic diseases		Pasteurellosis	t	t	MEDIUM	2026-01-22 13:47:28.996332	\N	\N	\N			f
a3cac7e7-00ba-42f9-a0b9-54f72c57836c	\N	2026-01-22 13:49:51.219311	African swine fever virus (ASFV) is a large, double-stranded DNA virus in the Asfarviridae family. It is the causative agent of African swine fever	ASF-001	African Swine Fever (ASF)	t	t	MEDIUM	2026-01-22 13:49:51.219311	\N	\N	\N			f
9e2a8bce-01ef-46a1-a969-c652aaba0581	\N	2026-01-22 13:50:43.400489	A highly contagious, economically devastating viral disease of pigs and wild boar, causing fever, lethargy, skin discoloration, and often death	CSF-001	Classical Swine Fever (CSF)	t	t	MEDIUM	2026-01-22 13:50:43.400489	\N	\N	\N			f
91e8d50b-f31e-40c9-abaf-af46152b8be2	\N	2026-01-22 13:52:03.857519	A respiratory disease in pigs caused by influenza A viruses (like H1N1, H3N2) that can occasionally jump to humans	SI-001	Swine Influenza	t	t	MEDIUM	2026-01-22 13:52:03.857519	\N	\N	\N			f
b8c92bc3-7ac0-4307-a633-fe59cae56450	\N	2026-01-22 13:57:36.620573	The symptoms include reproductive failure, pneumonia and increased susceptibility to secondary bacterial infection.	PRR-001	Porcine Reproductive and Respiratory Syndrome (PRRS)	t	t	HIGH	2026-01-22 13:57:36.620573	\N	\N	\N			f
991d987c-526e-435f-865e-384d5a9bf5e2	\N	2026-01-22 13:59:12.740571	PRRS is characterized by reproductive failure in boars and sows and systemic infection in growing-finishing pigs, and it is marked by respiratory disease	PED-001	Porcine Epidemic Diarrhoea (PED)	t	t	MEDIUM	2026-01-22 13:59:12.740571	\N	\N	\N			f
8cf0449d-3b2c-42ed-b511-f4130d8983e3	\N	2026-01-22 13:26:48.483095	Anthrax is an infectious disease caused by the encapsulated, spore-forming, gram-variable bacterium Bacillus anthracis.	A-001	Anthrax	t	t	MEDIUM	2026-01-22 13:59:25.048795	\N	\N	\N			f
24b41530-7dc0-4462-b00e-19da459eb7b3	\N	2026-01-22 14:00:57.628803	A common bacterial disease in animals, especially poultry, pigs, and cattle, caused by pathogenic strains of Escherichia coli	CO-001	Colibacillosis	t	t	HIGH	2026-01-22 14:11:20.334156	\N	\N	\N			f
9a664202-fb76-4aa5-9a77-b96c3ddb8366	\N	2026-01-22 13:49:01.462341	A common, highly contagious intestinal disease in young animals (poultry, livestock, pets)	C-001	Coccidiosis	t	t	MEDIUM	2026-01-22 14:12:09.441307	\N	\N	\N			f
4b84a8fe-6731-46e2-baf0-1f563dcd855c	\N	2026-01-22 13:48:14.827416	A common parasitic infection in humans and animals caused by helminths	HE-001	Helminthiasis	t	t	HIGH	2026-01-22 14:13:09.390449	\N	\N	\N			f
38e145ce-99c0-4484-8963-ff5b63421ecd	\N	2026-01-22 13:33:02.144789	Leptospirosis is a blood infection caused by bacteria of the genus Leptospira that can infect humans, dogs, rodents, and many other	LP-001	Leptospirosis	t	t	HIGH	2026-01-22 14:02:24.092863	\N	\N	\N			f
eb847972-3d56-49dc-a88c-d7a87aace91a	\N	2026-01-22 14:03:10.832195	A superficial bacterial skin infection, usually by Group A Streptococcus, causing a fiery red, swollen, tender, and sharply defined rash, often with fever and chills, typically on the face or legs	EP-001	Erysipelas	t	t	MEDIUM	2026-01-22 14:03:10.832195	\N	\N	\N			f
96ce7475-1c81-4f05-96c8-d8dd39742790	\N	2026-01-22 14:03:52.019627	An infection with a parasite called Toxoplasma gondii	TO-001	Toxoplasmosis	t	t	MEDIUM	2026-01-22 14:03:52.019627	\N	\N	\N			f
91a69ae1-f43f-4947-b319-069366b7562e	\N	2026-01-22 14:04:31.261844	A highly contagious skin disease in animals (especially dogs, foxes, pigs) caused by the *Sarcoptes scabiei	SM-001	Sarcoptic Mange	t	t	LOW	2026-01-22 14:04:31.261844	\N	\N	\N			f
b3e1a9eb-5973-4d08-8897-3f20350abd87	\N	2026-01-22 14:05:20.831506	A contagious viral disease affecting birds, caused by Influenza A viruses	AI-001	Avian Influenza (AI)	t	t	MEDIUM	2026-01-22 14:05:20.832506	\N	\N	\N			f
5b01cd9b-4d32-4c11-95af-f59b5468ee20	\N	2026-01-22 14:05:53.480358	A highly contagious and often fatal viral infection affecting poultry and other birds	ND-001	Newcastle Disease (ND)	t	t	HIGH	2026-01-22 14:05:53.480358	\N	\N	\N			f
fe594c55-a6b2-4fc0-a2b9-d5683e2a1402	\N	2026-01-22 14:06:27.7124	A highly contagious viral illness in young chickens, primarily affecting their immune system	IBD-001	Infectious Bursal Disease (IBD)	t	t	HIGH	2026-01-22 14:06:27.7124	\N	\N	\N			f
0ca048e2-190b-43d6-9b37-b869aefeb8f6	\N	2026-01-22 14:07:06.935205	A highly contagious respiratory disease in chickens	IB-001	Infectious Bronchitis	t	t	MEDIUM	2026-01-22 14:07:06.935205	\N	\N	\N			f
991b888d-abc4-4480-a2ac-18494b1295ba	\N	2026-01-22 14:07:47.600526	A slow-spreading viral disease in birds (chickens, turkeys, etc.) causing wart-like scabs on unfeathered skin	FP-001	Fowl Pox	t	t	MEDIUM	2026-01-22 14:07:47.600526	\N	\N	\N			f
154d5939-f92d-4764-8f94-604f53f70eec	\N	2026-01-22 14:08:18.995495	A highly contagious herpesvirus infection in chickens, causing tumors	MD-001	Marekâ€™s Disease	t	t	MEDIUM	2026-01-22 14:08:18.995495	\N	\N	\N			f
6f5ff598-88a7-4d69-b3b6-0c26e78fd79e	\N	2026-01-22 14:08:54.967189	A highly contagious viral disease in poultry, especially young chickens	AE-001	Avian Encephalomyelitis Infectious	t	t	MEDIUM	2026-01-22 14:08:54.967189	\N	\N	\N			f
fe2ea41b-9d2c-4bbc-b91b-572489d1161f	\N	2026-01-22 14:09:24.370253	It is the inflammation of larynx where the symptoms are loss of voice, harsh breathing	LA-001	Laryngotracheitis	t	t	MEDIUM	2026-01-22 14:09:24.370253	\N	\N	\N			f
c82bd4f1-3b6d-4e7f-b8c2-5677de051f51	\N	2026-01-22 14:10:18.130909	Pullorum disease and fowl typhoid are highly contagious, economically devastating poultry diseases	SAL-001	Salmonellosis (Pullorum disease, Fowl typhoid)	t	t	MEDIUM	2026-01-22 14:10:18.130909	\N	\N	\N			f
eb016295-5bd7-4420-9aca-a8cbc8ac0a0b	\N	2026-01-22 14:11:51.846926	Is a bacterium that can lead to bacterial infections	MY-001	Mycoplasmosis	t	t	MEDIUM	2026-01-22 14:11:51.846926	\N	\N	\N			f
0e3f6279-076c-452b-8268-3156d7c82990	\N	2026-01-22 14:12:48.003846	A fungal infection from inhaling common mold spores	AS-001	Aspergillosis	t	t	LOW	2026-01-22 14:12:48.003846	\N	\N	\N			f
\.


--
-- Data for Name: farm_animals; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.farm_animals (id, count, created_at, updated_at, animal_type_id, farm_id) FROM stdin;
e83e5b29-a889-4b85-acdb-215010737c26	200	2026-01-26 15:10:45.29173	2026-01-26 15:10:45.29173	e72c0140-5e78-4e29-9292-52406f7c300b	ef243267-d147-4784-aea2-15ce4fa1871b
bfeec05b-8d3d-4063-8f46-da5a3680e8f7	200	2026-01-26 15:11:57.653008	2026-01-26 15:11:57.653008	e72c0140-5e78-4e29-9292-52406f7c300b	db399a82-f283-478b-af2a-b2fff0f5d5c5
d56ef7de-eae8-4dc1-b6c5-f3d21c62666b	200	2026-01-26 15:13:38.950163	2026-01-26 15:13:38.950163	e72c0140-5e78-4e29-9292-52406f7c300b	ab32e8c3-9678-45c8-a163-00fb13244f40
1d39b611-febf-4327-a82b-bdab30e8ccde	200	2026-01-26 15:14:24.699224	2026-01-26 15:14:24.699224	e72c0140-5e78-4e29-9292-52406f7c300b	cb09826e-c389-4df7-a1f3-389742b34a51
b7b3a579-d215-4e95-8eae-998554b4a757	200	2026-01-26 15:15:29.784417	2026-01-26 15:15:29.784417	e72c0140-5e78-4e29-9292-52406f7c300b	e261d0a7-1be9-4f60-8d1a-3c4c4be46332
f89a920d-00b7-4c15-b375-e66e3b2aa523	200	2026-01-26 15:16:07.746307	2026-01-26 15:16:07.747308	e72c0140-5e78-4e29-9292-52406f7c300b	751e611c-3d91-4f68-bff1-0e233892c273
803b58be-3413-4caa-98f4-73723927b6ac	200	2026-01-26 15:16:52.289145	2026-01-26 15:16:52.289145	e72c0140-5e78-4e29-9292-52406f7c300b	57976916-ef68-4321-ae0d-7374bde3d8c4
3a8fd72b-513a-4902-b287-6178defdd51b	200	2026-01-26 15:17:44.056543	2026-01-26 15:17:44.056543	e72c0140-5e78-4e29-9292-52406f7c300b	54ecb014-80f6-417f-9cf4-60720e3c142e
d232012c-6942-4fa8-a193-ac2c072ae13e	200	2026-01-26 15:18:29.15289	2026-01-26 15:18:29.15289	e72c0140-5e78-4e29-9292-52406f7c300b	fcf6aee2-50ee-496a-8c32-5902d595437e
8b110176-5ca5-4c5a-a805-b58ab2975c58	200	2026-01-26 15:19:09.607505	2026-01-26 15:19:09.607505	e72c0140-5e78-4e29-9292-52406f7c300b	77c4151e-555c-492b-8753-ebeeb004b63f
88abbde0-b00b-45cd-991e-a9c8cfb2f202	200	2026-01-26 15:19:43.814194	2026-01-26 15:19:43.814194	e72c0140-5e78-4e29-9292-52406f7c300b	a6fc7e29-fde6-4372-8973-74d97b91ebd3
7cb988f0-b30a-4b82-a578-63b6f537340f	200	2026-01-26 15:20:24.377484	2026-01-26 15:20:24.377484	e72c0140-5e78-4e29-9292-52406f7c300b	289a78d0-e5f7-4f8f-8479-47cebb34ce5c
9d2cd4b8-ba69-4c62-8324-557ef3bd68af	200	2026-01-26 15:20:55.00929	2026-01-26 15:20:55.00929	e72c0140-5e78-4e29-9292-52406f7c300b	e9edf87b-6ad7-42e8-9259-b87f36557541
4ee70937-8250-457d-8b06-5da286679a3c	200	2026-01-26 15:21:28.179618	2026-01-26 15:21:28.179618	e72c0140-5e78-4e29-9292-52406f7c300b	6f81d75a-6738-47fb-9cdb-8db80021697d
c8dca487-5478-4727-9119-5d922addb44c	200	2026-01-26 15:22:05.907455	2026-01-26 15:22:05.907455	e72c0140-5e78-4e29-9292-52406f7c300b	aa36f3f9-8c27-4e9b-a9e5-c3ebb5a35559
f721d485-1d29-4613-9ea2-b6a33ea4657f	200	2026-01-26 15:22:38.767116	2026-01-26 15:22:38.767116	e72c0140-5e78-4e29-9292-52406f7c300b	a4abe78e-952e-4758-bbe5-4ff78ea50b60
70246fb3-51bf-47f8-a5d4-1454e3972922	200	2026-01-26 15:23:18.556995	2026-01-26 15:23:18.556995	e72c0140-5e78-4e29-9292-52406f7c300b	56f34b3d-103c-4259-b0d8-d74689435237
48b1e42a-e33f-418a-95ca-d0adb52d7617	200	2026-01-26 15:24:12.652303	2026-01-26 15:24:12.652303	e72c0140-5e78-4e29-9292-52406f7c300b	043fc85e-4b96-41a3-aee4-6b2b8e42b9e2
8c15733d-c8ec-477c-b9ec-4ea2d0ed9b4f	200	2026-01-26 15:24:47.262469	2026-01-26 15:24:47.262469	e72c0140-5e78-4e29-9292-52406f7c300b	d7934555-4b51-45cd-9671-9fbb7f80890c
0f653a4d-f43d-48c5-9bdb-7b7d640b4fbc	200	2026-01-26 15:25:18.717427	2026-01-26 15:25:18.717427	e72c0140-5e78-4e29-9292-52406f7c300b	e036fc45-3c0b-42cd-bb34-26c7a2768da4
7e2fa21d-3c3d-4431-b31b-645cda8fc6f7	200	2026-01-26 15:25:57.149503	2026-01-26 15:25:57.149503	e72c0140-5e78-4e29-9292-52406f7c300b	375b250f-71ba-41e4-ae2a-9ebc1c7d3da9
603b5578-9db0-49eb-8769-b827ebb6dfe9	200	2026-01-26 16:22:50.816091	2026-01-26 16:22:50.816091	e72c0140-5e78-4e29-9292-52406f7c300b	ebcb42d8-cd44-4ef4-8cb5-e570ec75cebe
bef2a9c3-77d1-485b-a906-e73820b81b2f	200	2026-01-26 16:24:19.129064	2026-01-26 16:24:19.129064	e72c0140-5e78-4e29-9292-52406f7c300b	ded4619c-e827-4d71-8403-438b675e970d
9c94b8af-2c2c-42ff-9758-d8164110ce2d	200	2026-01-26 16:24:48.712063	2026-01-26 16:24:48.712063	e72c0140-5e78-4e29-9292-52406f7c300b	740f9ffa-2678-4bfe-8056-67b661646e5d
50f66363-852a-48e5-a7bb-2e19227c7958	200	2026-01-26 16:25:24.340132	2026-01-26 16:25:24.340132	e72c0140-5e78-4e29-9292-52406f7c300b	0426d7c5-9cb9-44b6-92db-1423fa063ed1
01a7eddc-412d-474b-864c-2802d0d7286a	200	2026-01-26 16:26:52.040978	2026-01-26 16:26:52.040978	e72c0140-5e78-4e29-9292-52406f7c300b	c217e01f-1a79-4952-bdd6-29d111a2a48d
708cdda9-472d-402e-b524-9e84f08bd80a	200	2026-01-26 16:27:24.036794	2026-01-26 16:27:24.036794	e72c0140-5e78-4e29-9292-52406f7c300b	694e98c6-ea1b-42d6-b755-74d0b2861827
d3357e6f-de7c-49b4-96c8-1cedc0938cfb	200	2026-01-26 16:27:51.967108	2026-01-26 16:27:51.967108	e72c0140-5e78-4e29-9292-52406f7c300b	a0e29bb7-0c3c-437c-a542-45c6fafe089d
55098d74-17cc-440b-b20f-7679e4df1aa1	200	2026-01-26 16:28:26.1736	2026-01-26 16:28:26.1736	e72c0140-5e78-4e29-9292-52406f7c300b	e4faa691-d0ba-4942-b123-a4dac4cf936c
f5a2693c-d289-457c-8687-d8637e22eb29	200	2026-01-26 16:28:56.727129	2026-01-26 16:28:56.727129	e72c0140-5e78-4e29-9292-52406f7c300b	e6c2115f-6339-480c-ac11-1e3fb8178ca1
c9b1bf95-9069-4292-ba11-2eaeaa831a0e	400	2026-01-26 16:32:08.225611	2026-01-26 16:32:08.225611	e72c0140-5e78-4e29-9292-52406f7c300b	f7a3b47a-53a6-4e39-a3c5-00ce53d5f1fc
f2868a4a-c0cf-4ab7-959e-e3842d1c2fe3	400	2026-01-26 16:36:00.339196	2026-01-26 16:36:00.339196	e72c0140-5e78-4e29-9292-52406f7c300b	c84ae897-1b68-48e0-8c32-84504f8b89e8
a263fd9c-67b6-472e-bda4-1d7254fb2869	200	2026-01-26 16:38:11.959237	2026-01-26 16:38:11.959237	e72c0140-5e78-4e29-9292-52406f7c300b	3fbded90-988d-4ceb-8523-ab36cc1ab5bd
eae47f9f-bebf-4a63-a6cc-bb31948082d9	200	2026-01-26 16:38:59.24975	2026-01-26 16:38:59.24975	e72c0140-5e78-4e29-9292-52406f7c300b	6970219f-30b0-4c5f-99f5-b6a0606e6e62
5740d97d-58a6-4aed-b03f-19148a10ebb9	200	2026-01-26 16:39:50.649063	2026-01-26 16:39:50.649063	e72c0140-5e78-4e29-9292-52406f7c300b	2a4baeec-cfd7-4d4a-8174-9218b576c7b3
767a99df-4772-495f-acba-7be530f745b6	200	2026-01-26 16:40:42.96699	2026-01-26 16:40:42.96699	e72c0140-5e78-4e29-9292-52406f7c300b	4effeb95-3f05-4cec-8d76-fdd4afc7a4e3
5fdb0f20-fffc-4bee-85f6-a327947359ef	200	2026-01-26 16:41:21.700999	2026-01-26 16:41:21.700999	e72c0140-5e78-4e29-9292-52406f7c300b	e93fc0bb-f560-46a9-add6-a0cd755bd0f7
85fa0424-d9d6-4fac-ba1e-815cfd41e8d6	200	2026-01-26 16:41:50.644383	2026-01-26 16:41:50.644383	e72c0140-5e78-4e29-9292-52406f7c300b	d4b09b0c-e306-48a9-a4aa-32e85828a9b8
de9e0dde-31f3-4fd8-b59a-23eeec5f1b27	200	2026-01-26 16:42:26.80095	2026-01-26 16:42:26.80095	e72c0140-5e78-4e29-9292-52406f7c300b	0d477214-1ac7-4aa9-b75e-c02410330c19
77489180-1dc8-4f17-934f-4c2fd1cdbae8	200	2026-01-26 16:44:11.239176	2026-01-26 16:44:11.239176	e72c0140-5e78-4e29-9292-52406f7c300b	fc9f2b4e-ce73-401c-9f92-e15673bff90a
5891629d-bd9b-4441-be42-1e5492eee966	200	2026-01-26 16:45:38.726891	2026-01-26 16:45:38.726891	e72c0140-5e78-4e29-9292-52406f7c300b	b79bc8ee-2a84-4b84-a477-33f5dd8e98c4
02dd8e4b-00ee-4242-a70e-4056a4024b50	200	2026-01-26 16:46:13.324229	2026-01-26 16:46:13.324229	e72c0140-5e78-4e29-9292-52406f7c300b	c3f894cd-b9d6-4926-b4e0-bb6b0cc65c59
79500f37-80e3-426b-a98d-b6ab50125323	200	2026-01-26 16:46:54.716642	2026-01-26 16:46:54.716642	e72c0140-5e78-4e29-9292-52406f7c300b	c3b143cd-a142-4996-bfdf-32c54fcaba17
62aa98b4-78b2-4909-9a04-517adad00e1d	200	2026-01-26 16:47:35.112963	2026-01-26 16:47:35.112963	e72c0140-5e78-4e29-9292-52406f7c300b	d092a3a2-aea1-46a0-b9a8-00615764f4d5
f1d19fdd-1285-44e1-a228-78e848e351c7	300	2026-01-26 16:58:47.893092	2026-01-26 16:58:47.893092	e72c0140-5e78-4e29-9292-52406f7c300b	d7c3e876-a398-42e2-9b1f-3edf5cda7347
a169b79e-f70b-4530-b61a-81edf14227c5	300	2026-01-26 16:59:29.814021	2026-01-26 16:59:29.814021	e72c0140-5e78-4e29-9292-52406f7c300b	2b4f99f6-d556-434f-9bd9-5a1c7e33d6da
f6f38477-1dad-40f0-8bbc-a5003c630a45	300	2026-01-26 17:00:09.003429	2026-01-26 17:00:09.003429	e72c0140-5e78-4e29-9292-52406f7c300b	a390ab6d-99e3-4eb0-815b-3d24099c935e
3ab3d62f-4308-4e21-a669-b77ecf2bf1a8	300	2026-01-26 17:01:26.415949	2026-01-26 17:01:26.415949	e72c0140-5e78-4e29-9292-52406f7c300b	248d7281-9cb8-425d-aa64-065312c6ec7a
4f05bccc-3b0d-4ef6-8b8d-0c31710999fc	300	2026-01-26 17:01:59.713274	2026-01-26 17:01:59.713274	e72c0140-5e78-4e29-9292-52406f7c300b	aef68083-f999-4c56-827a-cb2e98304b07
8e1c09fe-31e5-4db7-bb76-1e6d2081ea61	300	2026-01-26 17:02:49.935319	2026-01-26 17:02:49.935319	e72c0140-5e78-4e29-9292-52406f7c300b	b70b752e-1801-4874-8484-fc739efbca62
8524cbb2-7e78-4c61-83f7-7019000ad306	300	2026-01-26 17:03:35.482512	2026-01-26 17:03:35.482512	e72c0140-5e78-4e29-9292-52406f7c300b	2573afc1-fa8c-490b-86e6-30021afd8023
178134e6-c04a-4dbf-b8d0-dd1c24a69ec1	300	2026-01-26 17:04:18.84898	2026-01-26 17:04:18.84898	e72c0140-5e78-4e29-9292-52406f7c300b	6d57b583-b041-4b3a-9332-520aee8a6db8
a866f406-b064-4a53-a40b-6b077c7eda36	400	2026-01-26 17:05:09.25066	2026-01-26 17:05:09.25066	e72c0140-5e78-4e29-9292-52406f7c300b	c42837e9-5490-4dcd-86d4-6c6ac0025718
695784ed-ed70-4f6d-b726-057055d2dda8	400	2026-01-26 17:05:51.800432	2026-01-26 17:05:51.800432	e72c0140-5e78-4e29-9292-52406f7c300b	8f3fc4d7-8ee1-46bb-9f3c-8eb0c65b22e3
693e7a4e-c871-46f6-a15e-0da71177a827	400	2026-01-26 17:08:29.458419	2026-01-26 17:08:29.458419	e72c0140-5e78-4e29-9292-52406f7c300b	3ad9756b-3704-4f73-a8d8-61ce380c21c6
a9b2da35-a589-4b40-af3a-7ee013e46599	400	2026-01-26 17:09:00.735033	2026-01-26 17:09:00.735033	e72c0140-5e78-4e29-9292-52406f7c300b	ccf799c9-6210-484c-b646-9af68b631112
99b8e5bb-a828-43f6-a07f-53de7ec7f15c	400	2026-01-26 17:09:41.846478	2026-01-26 17:09:41.846478	e72c0140-5e78-4e29-9292-52406f7c300b	52529e4f-a58c-4d1e-a31f-1668a83d20b3
5bfe8473-4ee0-4758-b97c-ba8d2e9fccc3	300	2026-01-26 17:10:54.451739	2026-01-26 17:10:54.451739	e72c0140-5e78-4e29-9292-52406f7c300b	4d34ac73-4a66-4e1e-9478-2eae3231a874
d347f952-2696-4e01-bc4c-d804f7deddbc	300	2026-01-26 17:11:38.484707	2026-01-26 17:11:38.484707	e72c0140-5e78-4e29-9292-52406f7c300b	36be09b0-41c8-4eeb-92f9-4f8b2755a7af
f6e99068-aed5-4754-b4d0-cf798fc43615	400	2026-01-26 17:12:10.703015	2026-01-26 17:12:10.703015	e72c0140-5e78-4e29-9292-52406f7c300b	34287f57-47ba-462c-b7a2-a31a19f064c7
0252a694-8b81-4ae8-99fb-aee65a8924a1	300	2026-01-26 17:13:23.288375	2026-01-26 17:13:23.288375	e72c0140-5e78-4e29-9292-52406f7c300b	6a3d6233-cd7c-4938-9814-09b54673f6a2
79fedb9a-871b-4924-a850-05c3780dd6fa	300	2026-01-26 17:14:53.914805	2026-01-26 17:14:53.914805	e72c0140-5e78-4e29-9292-52406f7c300b	2e37d740-6955-4b8f-b3e5-ebddfaa7da2c
6c17fa82-d6b7-4b40-aad8-5d29e36f113b	300	2026-01-26 17:15:24.289981	2026-01-26 17:15:24.289981	e72c0140-5e78-4e29-9292-52406f7c300b	77606368-aaaa-4d27-8245-3e50ea2b991a
8aa66968-981d-4d24-ba52-627aeb867381	300	2026-01-26 17:15:49.452238	2026-01-26 17:15:49.452238	e72c0140-5e78-4e29-9292-52406f7c300b	408e8b14-cf9c-4f32-a9ef-b077eb733bc7
1f835292-c506-48f1-aefd-d1dd20c7a820	400	2026-01-26 17:19:36.487353	2026-01-26 17:19:36.487353	e72c0140-5e78-4e29-9292-52406f7c300b	7af0668e-c5f6-4d03-be82-3fd102c31a87
3a4358ec-25d7-4f73-9fc7-280a73858cc5	4000	2026-01-26 17:28:45.296048	2026-01-26 17:28:45.296048	e72c0140-5e78-4e29-9292-52406f7c300b	03e86214-2262-42f8-af30-a95b93ec3953
f56446aa-a315-46b9-a5f2-eecd5e6085ee	5000	2026-01-26 17:29:13.135563	2026-01-26 17:29:13.138454	e72c0140-5e78-4e29-9292-52406f7c300b	5d111e98-c9b3-4c70-9bf7-4ee6eb1f07ac
1aa5ef28-9cb4-4159-a0c0-c4517bc9318c	4000	2026-01-26 17:30:29.277738	2026-01-26 17:30:29.277738	e72c0140-5e78-4e29-9292-52406f7c300b	502fe166-d09d-4cd7-a112-f8a0d09ef0f9
418a403d-f88f-496d-93e3-d4f9c1cee948	4000	2026-01-26 17:30:58.893518	2026-01-26 17:30:58.893518	e72c0140-5e78-4e29-9292-52406f7c300b	8a8afcf1-6a6c-469e-b204-a49856a94265
68b4a3bc-452b-4d25-9e0f-52a2c6a67e2c	4000	2026-01-26 17:31:33.799661	2026-01-26 17:31:33.799661	e72c0140-5e78-4e29-9292-52406f7c300b	b8e850a2-01b8-4043-8055-9e222b062c77
6fce4710-9cc5-4d27-bd7f-cdbc1ed51df3	4000	2026-01-26 17:32:05.611045	2026-01-26 17:32:05.611045	e72c0140-5e78-4e29-9292-52406f7c300b	fbd7b226-528a-4305-9a3b-cfdca7acbe77
71a1e5aa-e27e-4ef9-86d1-fe508cf43ef0	4000	2026-01-26 17:32:49.273189	2026-01-26 17:32:49.273189	e72c0140-5e78-4e29-9292-52406f7c300b	fb3382b0-8644-4526-b1bb-8be28bd7ef31
503df33b-4d7a-4968-8e22-8f8928f799b0	3000	2026-01-26 17:33:39.542683	2026-01-26 17:33:39.542683	e72c0140-5e78-4e29-9292-52406f7c300b	0e68f9e3-04fa-477f-bb49-a4f3dbf7cdcd
eb049e54-7756-4361-8ca2-58144144c5af	3000	2026-01-26 17:34:16.946088	2026-01-26 17:34:16.946088	e72c0140-5e78-4e29-9292-52406f7c300b	3b8a7600-d8eb-46c9-8ed6-c2b7bc184a49
a2664357-9ac3-4a9e-b4ea-6760269f410a	4000	2026-01-26 17:34:51.728025	2026-01-26 17:34:51.728025	e72c0140-5e78-4e29-9292-52406f7c300b	24fa12c6-a0f6-4ae9-b543-486bf49457d8
8599990d-49c0-4bcf-921d-44dc115f1d10	4000	2026-01-26 17:35:24.854951	2026-01-26 17:35:24.854951	e72c0140-5e78-4e29-9292-52406f7c300b	c700f3d6-95ce-41f8-9704-700e0d15327d
f6fb0644-ba91-49d3-9f45-5e08a8983527	4000	2026-01-26 17:36:01.718302	2026-01-26 17:36:01.718302	e72c0140-5e78-4e29-9292-52406f7c300b	d47b3856-d6ab-4719-a823-c5158e2e55b5
522d6ca4-00eb-4026-adb0-87060274dc88	4000	2026-01-26 17:36:33.796759	2026-01-26 17:36:33.796759	e72c0140-5e78-4e29-9292-52406f7c300b	a5890cc5-c2a5-401e-9cba-7187eaf352b8
1e0f9b2e-dd9e-4b31-8b6d-65629500e449	4000	2026-01-26 17:37:07.186695	2026-01-26 17:37:07.186695	e72c0140-5e78-4e29-9292-52406f7c300b	1fc47c21-a462-477d-a5e4-ebcbb8c82868
afe7ce09-6707-49cc-a9c6-73da1df60f06	400	2026-01-26 17:49:43.745508	2026-01-26 17:49:43.745508	e72c0140-5e78-4e29-9292-52406f7c300b	74bc557d-5a0d-4256-ae50-a5f44d2f8642
f9abe857-fcf9-448e-9d49-78e6bd23094a	300	2026-01-26 18:06:32.45973	2026-01-26 18:06:32.45973	e72c0140-5e78-4e29-9292-52406f7c300b	8780115d-36fa-4ba4-a303-1cc4a8173bb3
c076f1c9-0100-4bb0-9b53-fbcdc43add2b	400	2026-01-26 18:07:08.592069	2026-01-26 18:07:08.592069	e72c0140-5e78-4e29-9292-52406f7c300b	9b0f94a1-e5f1-4e19-a4ca-f669feb572f4
41de882c-34af-49e1-8087-91c20a6096f1	400	2026-01-26 18:07:43.091049	2026-01-26 18:07:43.091049	e72c0140-5e78-4e29-9292-52406f7c300b	0478fd9b-d3bd-475a-bf3f-eb4b30d7672a
c7896ce8-a6af-4556-a36d-3e7e663a72bb	400	2026-01-26 18:08:15.287916	2026-01-26 18:08:15.287916	e72c0140-5e78-4e29-9292-52406f7c300b	d14c69d5-e42a-4814-a157-9a8c37888e87
1f1fe994-d5b2-4bba-b0a8-2ee145edab9b	400	2026-01-26 18:08:47.502382	2026-01-26 18:08:47.502382	e72c0140-5e78-4e29-9292-52406f7c300b	ed405a85-b4da-4ae4-a8c7-4a400f21c156
b707d8d6-e872-4eb6-a507-c6fb4057dcfc	400	2026-01-26 18:09:15.334755	2026-01-26 18:09:15.334755	e72c0140-5e78-4e29-9292-52406f7c300b	1677d184-da20-489d-9de5-23ecf0a50c2c
4cfdb692-b4a8-4186-9daa-fda4bce948d4	400	2026-01-26 18:09:43.538533	2026-01-26 18:09:43.538533	e72c0140-5e78-4e29-9292-52406f7c300b	9aad0df3-5600-4f4f-ac19-de95496874cd
6aa71615-ebe8-4f63-bf41-379ece20bc26	400	2026-01-26 18:10:27.616576	2026-01-26 18:10:27.616576	e72c0140-5e78-4e29-9292-52406f7c300b	436116fc-c19e-4651-9acc-c1df63be7920
4a4ab9c9-c245-41c7-a5a8-5a15420f3326	400	2026-01-26 18:10:58.752327	2026-01-26 18:10:58.752327	e72c0140-5e78-4e29-9292-52406f7c300b	eb66f276-ea23-4729-b9ea-66c5167c0524
a7ec665f-ebf6-4001-b876-9c5e3fa10c59	500	2026-01-26 18:11:34.764404	2026-01-26 18:11:34.764404	e72c0140-5e78-4e29-9292-52406f7c300b	f03cc71c-5372-40cc-a300-e67d48195fab
85976069-b3ee-4bc4-821f-6d9741b12833	300	2026-01-26 18:20:01.062313	2026-01-26 18:20:01.062313	e72c0140-5e78-4e29-9292-52406f7c300b	80a79d80-cc0f-4c91-8faf-afb0ef358d31
fbef1a41-a23c-43f3-b6bd-4044c4d1499c	400	2026-01-26 18:20:33.38793	2026-01-26 18:20:33.38793	e72c0140-5e78-4e29-9292-52406f7c300b	270e4609-cfa4-4d4d-8c82-8d1a5485a701
16883ae6-1478-4c3b-8288-5ec8c1fa85b9	400	2026-01-26 18:21:00.219212	2026-01-26 18:21:00.219212	e72c0140-5e78-4e29-9292-52406f7c300b	89c7ee70-02c4-44a7-8d12-49f5b2d74cce
e4a2e996-2952-4a51-8681-48d2423eef25	400	2026-01-26 18:21:48.27601	2026-01-26 18:21:48.27601	e72c0140-5e78-4e29-9292-52406f7c300b	07e118f9-e502-490d-bc3c-98d5682f66c1
387147c7-6015-40e8-a5bd-3555edd34784	2000	2026-01-26 18:22:17.964308	2026-01-26 18:22:17.964308	e72c0140-5e78-4e29-9292-52406f7c300b	0afe5987-189f-4f70-a209-83c087fa125f
e1fc916c-d86e-4b69-acaf-2b08f34ada2d	400	2026-01-26 18:22:54.37262	2026-01-26 18:22:54.37262	e72c0140-5e78-4e29-9292-52406f7c300b	aff18dae-02e2-477e-90f6-ad7b1af5e8c8
353573a4-36d2-4044-90ca-73633dd105c7	400	2026-01-26 18:25:34.701423	2026-01-26 18:25:34.701423	e72c0140-5e78-4e29-9292-52406f7c300b	eaa55aca-64ae-4159-a440-174368bf742e
1a790670-9c96-4fd5-9f4b-26151ca9ecd2	400	2026-01-26 18:26:00.491481	2026-01-26 18:26:00.491481	e72c0140-5e78-4e29-9292-52406f7c300b	2d6c32a2-2908-4fde-9dc5-1cadf85c8a69
5408a8b6-d774-4acf-9774-0a3d5d5bdf2c	400	2026-01-26 18:26:35.255741	2026-01-26 18:26:35.255741	e72c0140-5e78-4e29-9292-52406f7c300b	56dd391d-62cf-462a-9175-77d27880d12d
c75688e1-b7fc-42c5-8c60-cf4ef0d342b1	400	2026-01-26 18:27:11.384937	2026-01-26 18:27:11.384937	e72c0140-5e78-4e29-9292-52406f7c300b	3feb4402-e37f-4833-ba8d-ddde7a4627e7
972c7b42-55b5-41c6-a75a-bf34730b73f0	400	2026-01-26 18:27:50.192997	2026-01-26 18:27:50.192997	e72c0140-5e78-4e29-9292-52406f7c300b	7b5f80ff-8150-4599-b13f-3ee49d3e44c2
b2adc24a-b27a-49b5-bebc-05f6d77ae2e3	400	2026-01-26 18:28:24.932323	2026-01-26 18:28:24.932323	e72c0140-5e78-4e29-9292-52406f7c300b	dffe29a4-7e7d-4dda-b6c6-794d38a8c790
cb5fd4be-638c-4647-bc7f-7b76efb1676d	400	2026-01-26 18:29:12.647055	2026-01-26 18:29:12.647055	e72c0140-5e78-4e29-9292-52406f7c300b	9031e672-c4d3-4a62-a159-39f574907f6c
5b87d778-0021-4814-9519-76562e9d276d	400	2026-01-26 18:29:38.788762	2026-01-26 18:29:38.788762	e72c0140-5e78-4e29-9292-52406f7c300b	e97d8896-8352-472b-8c8e-8bb3733eb3e8
763c4ee2-4a48-4aab-b3c9-14d2f415563e	400	2026-01-26 18:30:07.664665	2026-01-26 18:30:07.664665	e72c0140-5e78-4e29-9292-52406f7c300b	75d40ff5-ac82-4fee-9633-97f9a665d80d
f7352dcb-5b6b-4226-906c-ecfd3ace7f50	400	2026-01-26 18:30:37.422733	2026-01-26 18:30:37.422733	e72c0140-5e78-4e29-9292-52406f7c300b	72a64b1c-f41b-43ff-be12-e0fc73abc822
c8b658f8-b16b-4260-b665-0f351cc2db52	400	2026-01-26 18:31:20.419512	2026-01-26 18:31:20.419512	e72c0140-5e78-4e29-9292-52406f7c300b	5f2b4d04-108c-455b-a072-f38b5c7e8b15
8da84b60-07de-4e2b-9b76-b295206c30e1	400	2026-01-26 18:31:55.22984	2026-01-26 18:31:55.22984	e72c0140-5e78-4e29-9292-52406f7c300b	febab8fd-b06b-46ab-83f6-1ca5f908e1be
306d1680-ec62-4dfe-ac93-48fd8187517e	400	2026-01-26 18:32:22.631217	2026-01-26 18:32:22.631217	e72c0140-5e78-4e29-9292-52406f7c300b	094d805a-dc6d-4968-8d82-9edd1bc08095
c60d432d-75ca-4028-8fe3-97afe9e95796	400	2026-01-26 18:33:04.551513	2026-01-26 18:33:04.551513	e72c0140-5e78-4e29-9292-52406f7c300b	12625d8b-dbf4-4657-af54-aaaa8cea24a0
9dccf112-d658-4e4e-88c2-a6ae2e88fd14	400	2026-01-26 18:33:59.026893	2026-01-26 18:33:59.026893	e72c0140-5e78-4e29-9292-52406f7c300b	2eacdee2-9783-471e-bde6-51cda0f0b5c4
126ab32b-0a8f-4884-bfcf-ce8b8dd4c48e	400	2026-01-26 18:34:32.678298	2026-01-26 18:34:32.678298	e72c0140-5e78-4e29-9292-52406f7c300b	e76c787f-713b-4bfc-a21b-d2bef29716c8
7d4d7032-6a08-4dab-81ea-be5db2374a46	400	2026-01-26 18:42:31.942616	2026-01-26 18:42:31.942616	e72c0140-5e78-4e29-9292-52406f7c300b	cc2b867c-4cb8-4a84-9fac-ea328882ef41
5d36ca57-135e-4ed7-9dd3-a1e8c5c75889	400	2026-01-26 18:43:03.994408	2026-01-26 18:43:03.994408	e72c0140-5e78-4e29-9292-52406f7c300b	3ee5ee1f-5cce-4498-b5b0-6ff26d863bd1
c798ab9f-4efc-4a89-89f1-7704f42d14bb	400	2026-01-26 18:43:44.266936	2026-01-26 18:43:44.266936	e72c0140-5e78-4e29-9292-52406f7c300b	9a595b6a-26fe-44c7-86b4-aaa0e38be853
3072c3d8-9934-4766-ad3a-55c48f1ce5d0	400	2026-01-26 18:44:13.773693	2026-01-26 18:44:13.773693	e72c0140-5e78-4e29-9292-52406f7c300b	7377a7ac-b7ea-4cf3-8914-68abebcfad88
c46770b9-38b9-423f-950a-1cb6e5f5c8c3	400	2026-01-26 18:44:44.921439	2026-01-26 18:44:44.921439	e72c0140-5e78-4e29-9292-52406f7c300b	67ebc4ff-e780-4aae-aa4d-b8a3ca928456
fc38797d-6f22-4ca5-9d0b-148fa73d772a	400	2026-01-26 18:45:19.621442	2026-01-26 18:45:19.621442	e72c0140-5e78-4e29-9292-52406f7c300b	c6a07e12-e5c7-4382-9f6a-ba3a2a7ce71a
70113cd0-1aeb-483e-b9b4-c25ec9a7affb	800	2026-01-26 18:45:51.95609	2026-01-26 18:45:51.95609	e72c0140-5e78-4e29-9292-52406f7c300b	bc1bf4ef-2973-4110-9362-4d4e1033ae7d
ff7b3828-ba81-4de2-8b1a-c5ec4d62d244	4000	2026-01-26 19:21:36.324668	2026-01-26 19:21:36.324668	e72c0140-5e78-4e29-9292-52406f7c300b	f0e1a6e3-9881-45e9-9fc9-8fef3053df77
e09c97bf-d7d9-41e7-980f-a26e62a85608	500	2026-01-26 19:22:17.875451	2026-01-26 19:22:17.875451	e72c0140-5e78-4e29-9292-52406f7c300b	583ec334-45d8-408d-8741-0b8c6daab4aa
0e7d7cbe-3e66-4e61-b83c-9d2abe1e5f9f	4000	2026-01-26 19:23:16.497792	2026-01-26 19:23:16.497792	e72c0140-5e78-4e29-9292-52406f7c300b	f7cc17b9-2dd2-43ec-889a-fcc60eb07f4c
c72aa0bc-103a-41c4-a2d5-681b1074f121	500	2026-01-26 19:23:46.413547	2026-01-26 19:23:46.413547	e72c0140-5e78-4e29-9292-52406f7c300b	81fa5008-0d1f-47bc-b607-f0285228fcf0
6c5da8be-69cc-4309-b27a-e5678ef00ad5	600	2026-01-26 19:24:13.962318	2026-01-26 19:24:13.962318	e72c0140-5e78-4e29-9292-52406f7c300b	429bf0d3-1b8a-4e40-bae6-b527795f9021
5504dd66-9577-4077-b1ca-ae29a94351e5	400	2026-01-26 19:24:45.523683	2026-01-26 19:24:45.523683	e72c0140-5e78-4e29-9292-52406f7c300b	eb799bac-acc4-49b4-a736-744f1b46c81f
c84980fc-14d8-4f1a-94f4-96595b8b6d03	700	2026-01-26 19:26:15.801343	2026-01-26 19:26:15.801343	e72c0140-5e78-4e29-9292-52406f7c300b	f19fe104-e638-43a7-a58e-f2346e6cbf41
b71d9138-c5db-49ab-9768-b15dd6c24bf3	3000	2026-01-26 19:26:49.12944	2026-01-26 19:26:49.12944	e72c0140-5e78-4e29-9292-52406f7c300b	8a30c051-299b-43b4-a599-0382a8aeca95
b1035a1f-9937-457d-a5b5-7e51d81131ed	2000	2026-01-26 19:27:16.312099	2026-01-26 19:27:16.312099	e72c0140-5e78-4e29-9292-52406f7c300b	66cc87a6-e7fa-4195-b1a2-e1ca55e904e8
37bd4655-d635-40bf-b4d5-ab4b83538602	500	2026-01-26 19:27:43.530108	2026-01-26 19:27:43.530108	e72c0140-5e78-4e29-9292-52406f7c300b	31b3b356-4f6f-4deb-9369-8c08e3325ba9
c5c47b67-ba66-49dc-b881-b573d819fcfa	400	2026-01-26 19:28:39.665225	2026-01-26 19:28:39.665225	e72c0140-5e78-4e29-9292-52406f7c300b	5f9f46aa-ce8b-4997-9dfb-9607abef2e7b
abb34931-c9f3-48fa-88a2-65feca5623c6	200	2026-01-26 19:29:09.560742	2026-01-26 19:29:09.560742	e72c0140-5e78-4e29-9292-52406f7c300b	9be18bdc-263b-4ffd-b3fe-4ce0f4b73150
3b190d10-d9de-41ba-b498-9a0de8513c06	300	2026-01-26 19:29:40.393802	2026-01-26 19:29:40.393802	e72c0140-5e78-4e29-9292-52406f7c300b	b17cc979-4219-40a8-a253-e088b47fbfe6
27fced30-f097-47cd-baa9-ebbda72ca1f7	300	2026-01-26 19:30:07.084952	2026-01-26 19:30:07.084952	e72c0140-5e78-4e29-9292-52406f7c300b	ec785ed1-4c85-4e5d-a4e8-839c09b46ae9
967475eb-ded0-4c7b-b796-b8dd7c690190	400	2026-01-26 19:30:39.873654	2026-01-26 19:30:39.873654	e72c0140-5e78-4e29-9292-52406f7c300b	95fb8319-8c3c-4f64-8a0f-b3128490396e
52322e35-da73-4d32-b97b-80fe54646e60	3000	2026-01-26 19:38:59.800176	2026-01-26 19:38:59.800176	e72c0140-5e78-4e29-9292-52406f7c300b	21cd575c-2d39-47a3-84be-9cf764a5127c
8ecd98a6-10ca-4e5a-b20c-09adeaae3396	400	2026-01-26 19:41:13.461069	2026-01-26 19:41:13.461069	e72c0140-5e78-4e29-9292-52406f7c300b	595d8e26-4956-45e7-9ea5-dc135bd7d674
fdddfc9e-49bd-4d4b-82fc-5d3b54854b1e	300	2026-01-26 19:41:41.946727	2026-01-26 19:41:41.946727	e72c0140-5e78-4e29-9292-52406f7c300b	de1d801a-9e72-402c-b6c7-6433eb943e16
837fe54f-9f9f-440f-aaaa-23136690ac4e	300	2026-01-26 19:42:11.385046	2026-01-26 19:42:11.385046	e72c0140-5e78-4e29-9292-52406f7c300b	4518a71e-9aae-4d59-bebf-8c874313faf3
e964a754-73b5-4473-97e8-1f52591f792d	3000	2026-01-26 19:42:45.217003	2026-01-26 19:42:45.217003	e72c0140-5e78-4e29-9292-52406f7c300b	29255e56-38a1-4cbb-9684-af70e2d58dfc
285760fe-dece-43ad-8492-12a2f9513c71	200	2026-01-26 19:47:47.270794	2026-01-26 19:47:47.270794	e72c0140-5e78-4e29-9292-52406f7c300b	e7c00d2e-4f10-44f0-9c02-f3ff3308f00a
8939d2a3-bdf7-46bd-a643-4a3f6b93ac39	1000	2026-01-26 19:48:21.174861	2026-01-26 19:48:21.174861	e72c0140-5e78-4e29-9292-52406f7c300b	258d33f5-f348-4479-9e8d-feacdeb65f80
bfed8217-00e8-48a8-b42b-de6a72234797	200	2026-01-26 19:48:50.542257	2026-01-26 19:48:50.542257	e72c0140-5e78-4e29-9292-52406f7c300b	937ff8ec-4520-4593-9bd2-0bda3902c29e
d4294c6f-54ff-453f-b0ef-c3168750049f	300	2026-01-26 19:49:21.714152	2026-01-26 19:49:21.714152	e72c0140-5e78-4e29-9292-52406f7c300b	ac26e74d-0eae-4b25-9c8b-c77a8e7c6929
25c933ac-1198-4bce-830d-eab1f87e6e3c	200	2026-01-26 19:49:46.324038	2026-01-26 19:49:46.324038	e72c0140-5e78-4e29-9292-52406f7c300b	f5fd144d-192d-4c13-97d6-9f7b5215ae02
15b4f5a8-0502-4830-a4c1-9a94bc28a561	300	2026-01-26 21:06:53.738021	2026-01-26 21:06:53.738021	e72c0140-5e78-4e29-9292-52406f7c300b	526211c9-cb82-47a4-8fa0-fd15c126f03f
fed830f3-8b58-45f1-af37-a19532715fd5	200	2026-01-26 21:07:26.264574	2026-01-26 21:07:26.264574	e72c0140-5e78-4e29-9292-52406f7c300b	eed37728-8aaf-4768-9862-570f31137f21
a1d44fa1-218d-4e1e-a794-84f0c6f4eafa	10000	2026-01-26 21:08:00.284495	2026-01-26 21:08:00.284495	e72c0140-5e78-4e29-9292-52406f7c300b	808bc30c-2f12-4779-8817-cc1313a3fd0c
e38f5bc2-ad37-48a2-9d36-c8cb888d0390	200	2026-01-26 21:09:00.655855	2026-01-26 21:09:00.661867	e72c0140-5e78-4e29-9292-52406f7c300b	1aff661a-2868-4608-a0de-c9a0930b2bc1
07004fe2-520e-4bde-86bc-f9ab78dd8d92	1000	2026-01-26 21:11:50.615759	2026-01-26 21:11:50.615759	e72c0140-5e78-4e29-9292-52406f7c300b	81ac85a3-30bf-435b-ad7b-68c115be52b9
1b6873a9-a5eb-433f-bbd3-17d283f97feb	200	2026-01-26 21:12:27.166783	2026-01-26 21:12:27.166783	e72c0140-5e78-4e29-9292-52406f7c300b	f3b340b3-1eb8-4ed0-9e2d-6ac315d27f25
3819274d-ae2f-4765-a830-9ff9093ec085	200	2026-01-26 21:13:14.16778	2026-01-26 21:13:14.16778	e72c0140-5e78-4e29-9292-52406f7c300b	bba20175-5ca7-4979-8c61-cc1596ca6401
85fe1f49-f897-4ef9-8353-fd83ec6ae412	200	2026-01-26 21:13:50.303897	2026-01-26 21:13:50.303897	e72c0140-5e78-4e29-9292-52406f7c300b	ef5bea47-5253-4c8c-a247-6155850e87a2
d00be26c-1c70-4518-8516-a3b68d491efa	200	2026-01-26 21:14:14.715666	2026-01-26 21:14:14.715666	e72c0140-5e78-4e29-9292-52406f7c300b	2654becc-055b-4443-942b-3d83a7d8d232
e4abe338-47ca-40f8-bb7f-f93b53cc6a0c	200	2026-01-26 21:14:42.025916	2026-01-26 21:14:42.025916	e72c0140-5e78-4e29-9292-52406f7c300b	31d30778-3b8e-4d74-908c-975355497c09
1d8f3d59-d786-484a-998e-112993b4c6e2	200	2026-01-26 21:18:18.080365	2026-01-26 21:18:18.080365	e72c0140-5e78-4e29-9292-52406f7c300b	cc49b0bb-5fc1-4141-9ba1-61f3fef971ce
47f12a86-ce1b-4ee7-94ba-93a09f80d6c0	200	2026-01-26 21:18:49.184033	2026-01-26 21:18:49.184033	e72c0140-5e78-4e29-9292-52406f7c300b	14478c31-6429-46b5-a621-8f32ef65f14e
36763eb2-2521-476f-ab6e-066443d19724	300	2026-01-26 21:19:28.00008	2026-01-26 21:19:28.00008	e72c0140-5e78-4e29-9292-52406f7c300b	0a46b083-5b65-49dc-8c40-092b1c51c56e
cb0dd0ca-2824-42e4-863f-c4e3b3d05425	200	2026-01-26 21:19:58.21651	2026-01-26 21:19:58.21651	e72c0140-5e78-4e29-9292-52406f7c300b	05b8eec4-8f03-458f-b3af-0271d786e08d
d8b1e2f0-e037-455f-9af9-28f7242393a4	200	2026-01-26 21:20:27.54816	2026-01-26 21:20:27.54816	e72c0140-5e78-4e29-9292-52406f7c300b	5b687388-fa3a-4390-8999-473214060280
06e3e317-168d-403e-9dcf-6d868d825c06	100	2026-01-26 21:20:58.450186	2026-01-26 21:20:58.450186	e72c0140-5e78-4e29-9292-52406f7c300b	6d779d64-21aa-4245-afc5-bac349a64614
37eb3753-863e-464a-be99-cd7648b61acb	100	2026-01-26 21:21:25.536444	2026-01-26 21:21:25.536444	e72c0140-5e78-4e29-9292-52406f7c300b	32008ee1-8a15-4ef1-9016-f011c44d100a
4e25d9d2-3430-4f23-83a8-60246968f577	200	2026-01-26 21:21:52.660212	2026-01-26 21:21:52.660212	e72c0140-5e78-4e29-9292-52406f7c300b	ee87a62e-ceed-43ca-ad08-5a6181382604
6f35d342-6976-4065-82ff-7c9951e37b9c	100	2026-01-26 21:23:30.576233	2026-01-26 21:23:30.576233	e72c0140-5e78-4e29-9292-52406f7c300b	7f3e82cf-dc79-46e7-b7c9-3fa93e8c2e4a
737f44d6-f637-4a1c-b466-fae6e8fbda21	100	2026-01-26 21:24:00.412379	2026-01-26 21:24:00.412379	e72c0140-5e78-4e29-9292-52406f7c300b	0bd7b00e-f163-404b-b32c-ac99b965c941
26ba1873-7a5d-47b3-98b2-ac659f51a861	100	2026-01-26 21:25:31.294121	2026-01-26 21:25:31.294121	e72c0140-5e78-4e29-9292-52406f7c300b	e8dc25bf-6d01-4038-9cd5-60c61f5b47b6
6ba01664-f17f-40ec-a815-02cfbed7a949	100	2026-01-26 21:27:49.541919	2026-01-26 21:27:49.541919	e72c0140-5e78-4e29-9292-52406f7c300b	17933e60-ab6f-4583-912c-78f87201a24b
ee5b0a29-8def-47a3-b269-28e24932f6e3	100	2026-01-26 21:29:22.314187	2026-01-26 21:29:22.314187	e72c0140-5e78-4e29-9292-52406f7c300b	2656606a-bca0-4fff-a250-d5f3fb8cbaae
20b4e624-02e7-4dcd-a0e8-ebeaf32eb675	100	2026-01-26 21:31:18.748644	2026-01-26 21:31:18.748644	e72c0140-5e78-4e29-9292-52406f7c300b	b9facc28-0b0b-4056-a3ce-1092464669f4
d06e4ee9-39c4-43e8-8470-e1b13f27b4e5	100	2026-01-26 21:22:30.441077	2026-01-26 21:22:30.441077	e72c0140-5e78-4e29-9292-52406f7c300b	de5947c1-0dc2-400a-837a-e0f69216b883
e8935b14-a5cf-49d7-b05f-d2aa4ef6b16c	100	2026-01-26 21:23:04.524599	2026-01-26 21:23:04.524599	e72c0140-5e78-4e29-9292-52406f7c300b	464d2563-aee0-48fb-80c5-35cd57c507a0
6399e470-09ef-4dd5-b736-6e31dd3a6009	2000	2026-01-26 21:26:49.265596	2026-01-26 21:26:49.265596	e72c0140-5e78-4e29-9292-52406f7c300b	74f03f06-d787-4d6b-833c-ae0f9f9be537
67c723b9-9570-4ef1-9803-cca74428c0b1	100	2026-01-26 21:27:23.684098	2026-01-26 21:27:23.684098	e72c0140-5e78-4e29-9292-52406f7c300b	b29d550f-af7d-4ed5-afac-e9c4445020f9
ad7e6bc6-cb3a-4826-89a8-004f63f21bf0	100	2026-01-26 21:28:19.862807	2026-01-26 21:28:19.862807	e72c0140-5e78-4e29-9292-52406f7c300b	569ad50b-9523-4271-8e3f-696b1fcb9df5
70a20e25-814f-4c22-acdd-07576455c73e	30000	2026-01-26 21:28:52.024382	2026-01-26 21:28:52.024382	e72c0140-5e78-4e29-9292-52406f7c300b	115fb6c6-8694-44c9-819f-68a66fbbfb61
26d31496-df79-4b65-b6dc-512d0ec9ac12	100	2026-01-26 21:29:48.759029	2026-01-26 21:29:48.759029	e72c0140-5e78-4e29-9292-52406f7c300b	afec7aa1-fbf6-40bd-a7ed-a85c0896283f
86c0f432-e993-4ddf-a529-3233f0bf2d20	100	2026-01-26 21:30:15.074218	2026-01-26 21:30:15.074218	e72c0140-5e78-4e29-9292-52406f7c300b	8ee885ce-cd59-4839-bff0-4cf8989e56e7
bda45522-c139-4e56-8c9e-53455ea1a8b1	100	2026-01-26 21:30:51.274587	2026-01-26 21:30:51.274587	e72c0140-5e78-4e29-9292-52406f7c300b	2ba4af9c-bbe9-4453-90ac-8221e0639195
faa74ed5-9775-46a4-b9c2-14b12a031393	100	2026-01-26 21:24:29.070797	2026-01-26 21:24:29.070797	e72c0140-5e78-4e29-9292-52406f7c300b	1024b8d1-2902-4a32-a54e-6753db32267f
4ac1c247-3632-485c-a0a6-667aac17095f	100	2026-01-26 21:24:59.710407	2026-01-26 21:24:59.710407	e72c0140-5e78-4e29-9292-52406f7c300b	49d42310-90ea-4dd1-8765-f2f7ef89416d
bfbbc0cd-61ba-45a5-8620-8f6012e27997	100	2026-01-26 21:26:11.896168	2026-01-26 21:26:11.896168	e72c0140-5e78-4e29-9292-52406f7c300b	dc877c5b-724c-4c68-a9e6-7f9169fff0a9
0b8133b3-f4d1-4380-9d31-74274a53f7f0	100	2026-01-26 21:31:45.274396	2026-01-26 21:31:45.274396	e72c0140-5e78-4e29-9292-52406f7c300b	291bfe25-a3d9-4c66-9b13-25a3db7dbcd5
d028dbe6-fb70-42b1-9d53-0cc239b54336	100	2026-01-26 21:32:10.98923	2026-01-26 21:32:10.98923	e72c0140-5e78-4e29-9292-52406f7c300b	978252da-3e15-4a32-92b4-b25f55a8f8bd
7950c953-ba34-4b76-8e12-8555790b4b1f	200	2026-01-26 21:32:37.854366	2026-01-26 21:32:37.854366	e72c0140-5e78-4e29-9292-52406f7c300b	1040de28-02b2-48df-8ae1-37a70530669f
d8b1da6d-6bf0-4937-9de5-85f71f9414ff	100	2026-01-26 21:33:02.804449	2026-01-26 21:33:02.804449	e72c0140-5e78-4e29-9292-52406f7c300b	88a72d8f-7d07-4c7d-b8b1-08ccdb7ffe99
f189d11f-a208-4caa-a98b-d2e04a3017bb	100	2026-01-26 21:39:52.003998	2026-01-26 21:39:52.003998	e72c0140-5e78-4e29-9292-52406f7c300b	0e21faf4-9082-4d8a-8c6d-b4c9b67729d2
f136603a-b9fc-447a-9aa9-ca3f06405bc6	100	2026-01-26 21:40:48.336583	2026-01-26 21:40:48.336583	e72c0140-5e78-4e29-9292-52406f7c300b	44b63d4b-2ee9-498a-9dc4-3400df14e9f9
d6277537-c356-4d9a-b2a9-03ca41159287	300	2026-01-26 21:41:56.466894	2026-01-26 21:41:56.466894	e72c0140-5e78-4e29-9292-52406f7c300b	a08046d3-b77e-4408-9562-161c586b8559
148257ae-e5bd-4875-aed5-47bf1d7e896a	400	2026-01-26 21:42:27.266902	2026-01-26 21:42:27.266902	e72c0140-5e78-4e29-9292-52406f7c300b	89006adb-0a53-4aa6-a956-7e8b6b804281
f08695ee-5108-4f4d-b394-861ad81c8f96	400	2026-01-26 21:42:57.646262	2026-01-26 21:42:57.646262	e72c0140-5e78-4e29-9292-52406f7c300b	566f402d-ef2d-44e6-b319-dc32a40f7703
5d6775d2-59d3-4513-9b35-ec58f3f2b4f5	500	2026-01-26 21:43:37.749575	2026-01-26 21:43:37.749575	e72c0140-5e78-4e29-9292-52406f7c300b	996e5154-2247-45cc-9113-4f9dd5f378a5
e82dc3fb-2db4-4871-9daf-5660db75b6fd	200	2026-01-26 21:44:06.924475	2026-01-26 21:44:06.924475	e72c0140-5e78-4e29-9292-52406f7c300b	7601d450-5bf4-42d8-810f-35bcffe44f46
4e69c424-a571-4809-99bf-66dec5cee199	200	2026-01-26 21:44:39.607821	2026-01-26 21:44:39.607821	e72c0140-5e78-4e29-9292-52406f7c300b	3d5236aa-a4da-413a-8eee-e20c270265e2
62e66319-e728-4241-827f-fad41749e00f	100	2026-01-26 21:45:23.61584	2026-01-26 21:45:23.61584	e72c0140-5e78-4e29-9292-52406f7c300b	2ce70e93-cf96-4219-9408-b17b40e78252
832629b2-740a-4c34-8185-9df699a51838	100	2026-01-26 21:46:09.028536	2026-01-26 21:46:09.028536	e72c0140-5e78-4e29-9292-52406f7c300b	f13f93b8-1c8a-4fb4-9e5c-fd4691fcd732
d59cebcb-83a8-4c62-b978-7e85a60b421f	100	2026-01-26 21:46:40.848083	2026-01-26 21:46:40.848083	e72c0140-5e78-4e29-9292-52406f7c300b	5c143f73-49af-4b0e-8fc3-e41e4e55ed66
6614e5c7-78e3-44bd-836d-fde0d1f5cde0	100	2026-01-26 21:47:17.479473	2026-01-26 21:47:17.479473	e72c0140-5e78-4e29-9292-52406f7c300b	64b5908d-64dd-4ec3-824d-08d5650772a8
1904a6d5-b24c-4293-81c8-0ec5a246b04f	100	2026-01-26 21:47:48.274933	2026-01-26 21:47:48.274933	e72c0140-5e78-4e29-9292-52406f7c300b	fec1a952-c810-4b0f-aa73-4c0199b6931a
79757109-377d-422d-902c-1ff7b2f9ea96	100	2026-01-26 21:48:13.427727	2026-01-26 21:48:13.427727	e72c0140-5e78-4e29-9292-52406f7c300b	c6d43015-50ea-42d3-84f8-c261704eeda0
7a43f468-c593-48a8-85d3-f0889d027257	300	2026-01-26 21:48:41.628713	2026-01-26 21:48:41.628713	e72c0140-5e78-4e29-9292-52406f7c300b	124404e8-ad21-4845-b9b9-7e38a21fad61
feef2411-11c7-42dd-b2e4-ee0c1fbc7a4a	500	2026-01-26 21:49:26.176742	2026-01-26 21:49:26.176742	e72c0140-5e78-4e29-9292-52406f7c300b	9e07b8d7-f137-464f-8f89-d506894f2ca1
123a3453-f003-4a1a-b410-00715735272a	100	2026-01-26 21:54:47.951797	2026-01-26 21:54:47.951797	e72c0140-5e78-4e29-9292-52406f7c300b	11f44d20-7ed1-4f52-b077-008ad4e75beb
eb370a21-581b-4f32-ac7d-b8c7b89c4df7	200	2026-01-26 21:55:21.628764	2026-01-26 21:55:21.628764	e72c0140-5e78-4e29-9292-52406f7c300b	ed147a3b-dc32-4a9b-9f90-f8be23876028
fd35b69e-e400-44ec-bbec-43b0d0d4a96a	300	2026-01-26 21:55:47.636154	2026-01-26 21:55:47.636154	e72c0140-5e78-4e29-9292-52406f7c300b	2fc9dad2-b4a0-4e5a-8bb9-36f560de3459
94252582-e628-4ec3-bd2b-cbd25958e962	200	2026-01-26 22:11:23.619915	2026-01-26 22:11:23.619915	e72c0140-5e78-4e29-9292-52406f7c300b	a7b2bff8-f3af-44c1-b94f-4f7fee43df02
dad5dd3a-a4e7-4272-b207-49921b592ba2	100	2026-01-26 22:12:11.379347	2026-01-26 22:12:11.379347	e72c0140-5e78-4e29-9292-52406f7c300b	ca1fb1ce-9a1c-4ed4-ae5a-349a5d623f86
15f2fd29-c721-4c97-9e49-5a7d1e8d7243	2000	2026-01-26 22:12:44.448661	2026-01-26 22:12:44.448661	e72c0140-5e78-4e29-9292-52406f7c300b	726cbe0a-861d-4709-b536-fdbc084c2f71
f07eb6d8-5cea-43c5-9d40-fcfddde9e9a3	200	2026-01-26 22:13:29.967164	2026-01-26 22:13:29.967164	e72c0140-5e78-4e29-9292-52406f7c300b	a5b922b0-ac04-4ef3-ba67-1090ee5ce2b4
61d4128f-4089-48e9-ad04-b3804e930ace	200	2026-01-26 22:14:08.513093	2026-01-26 22:14:08.513093	e72c0140-5e78-4e29-9292-52406f7c300b	98f384bf-dc94-411c-b7ca-0aefd0e5186c
4ae2d2ac-1ee0-4626-806a-1eb3f9004793	300	2026-01-26 22:14:45.849094	2026-01-26 22:14:45.849094	e72c0140-5e78-4e29-9292-52406f7c300b	cb1de2b8-8497-42bd-90f4-b0610daf085f
99cd0892-10d8-4c88-95f2-3be6dba13f40	200	2026-01-26 22:15:28.295663	2026-01-26 22:15:28.295663	e72c0140-5e78-4e29-9292-52406f7c300b	b4bef680-c679-47a0-ab0e-0e7104b90947
b5897e1c-359b-4a73-ba22-7a5f084329cb	500	2026-01-26 22:16:03.922032	2026-01-26 22:16:03.922032	e72c0140-5e78-4e29-9292-52406f7c300b	ce435824-26e2-45d1-85a2-3357a6ca6993
c365fce0-9bc3-40a9-a706-3cc226adcffb	100	2026-01-26 22:16:32.925606	2026-01-26 22:16:32.925606	e72c0140-5e78-4e29-9292-52406f7c300b	e516899b-e8bc-4cec-bf1e-650fa2f94dfd
03cdce4c-43fb-4f28-bf6a-47b440d2fe3a	200	2026-01-26 22:22:00.44583	2026-01-26 22:22:00.44583	e72c0140-5e78-4e29-9292-52406f7c300b	741c2838-437a-4715-a451-24975abffaf2
2c8c78bd-4d45-4201-82af-e3a63ba1a53a	300	2026-01-26 22:22:33.044131	2026-01-26 22:22:33.044131	e72c0140-5e78-4e29-9292-52406f7c300b	43d34dc2-0894-4eb8-9ab3-bf49c5a212de
40527b2b-2f70-49e9-83b8-d77b33febd9f	500	2026-01-26 22:23:34.420624	2026-01-26 22:23:34.420624	e72c0140-5e78-4e29-9292-52406f7c300b	e5c5b417-b034-4ddf-923a-4fae1d30b248
548f93bc-6730-4891-a8b7-3eb359cf9bfe	200	2026-01-26 22:24:43.240893	2026-01-26 22:24:43.240893	e72c0140-5e78-4e29-9292-52406f7c300b	f3a91aa9-4bfa-4527-8d02-6cf3399dc9ef
d868f1e0-1e51-45b1-8f1d-e3c4e0898ce1	3000	2026-01-26 22:25:18.259709	2026-01-26 22:25:18.259709	e72c0140-5e78-4e29-9292-52406f7c300b	83cd4d4b-c1d3-405a-b8c9-582f57b6d451
cb3bc0b8-d93c-4664-9b73-e898376d01ea	200	2026-01-26 22:25:59.845568	2026-01-26 22:25:59.845568	e72c0140-5e78-4e29-9292-52406f7c300b	e51c1ae5-d16f-4d18-b1e8-9dff64a84438
13ed4c38-623f-40b8-86ae-e3f669d8d0eb	200	2026-01-26 22:27:04.38576	2026-01-26 22:27:04.38576	e72c0140-5e78-4e29-9292-52406f7c300b	493d9911-17e5-441d-88c2-a46e63d43208
2da96106-69a9-477f-bfac-8437754554c5	200	2026-01-26 22:27:40.330685	2026-01-26 22:27:40.330685	e72c0140-5e78-4e29-9292-52406f7c300b	46f5cb53-11d3-4dc5-a22b-dd73eebb9ffd
e8743c93-1e87-4cd1-904d-b6a9a3397d66	500	2026-01-26 22:28:13.44772	2026-01-26 22:28:13.44772	e72c0140-5e78-4e29-9292-52406f7c300b	3c139070-82c3-483c-aca3-28a6fded2ddc
16d68973-f6b5-4909-a578-3dc77272dfe7	200	2026-01-26 22:30:00.333007	2026-01-26 22:30:00.333007	e72c0140-5e78-4e29-9292-52406f7c300b	d7019a29-8d24-40fa-8a3a-438dbe521bd8
e86fce21-329f-43af-8478-b0704afb1806	200	2026-01-26 22:30:48.663428	2026-01-26 22:30:48.663428	e72c0140-5e78-4e29-9292-52406f7c300b	21a7b528-9327-4700-a142-551053836ce0
96ddf82c-10ca-47aa-89c6-d4fd80e56dab	200	2026-01-26 22:32:40.910699	2026-01-26 22:32:40.910699	e72c0140-5e78-4e29-9292-52406f7c300b	7db18ba8-f269-42bc-b6bb-dc07c3fc5112
178b6fc6-b848-4089-b9cc-e54d584b6a3f	200	2026-01-26 22:33:18.349633	2026-01-26 22:33:18.349633	e72c0140-5e78-4e29-9292-52406f7c300b	f4e99e9b-6c98-4cc0-8325-356616abda3a
6ee175dd-23a2-4b9c-833b-3a6445273aa3	400	2026-01-26 22:33:55.984697	2026-01-26 22:33:55.984697	e72c0140-5e78-4e29-9292-52406f7c300b	19a2626f-85bf-4555-bf7c-4027cf1d1dc2
7b33de63-1f72-472e-8a09-a4bff7e6178e	300	2026-01-26 22:34:33.72614	2026-01-26 22:34:33.72614	e72c0140-5e78-4e29-9292-52406f7c300b	2fd3e25b-9dc1-41d2-b8f7-32a105fcaeee
9db8e5aa-7124-448c-9835-f7347dda0468	200	2026-01-26 22:35:02.404926	2026-01-26 22:35:02.404926	e72c0140-5e78-4e29-9292-52406f7c300b	ead95db6-2ea2-4c5f-aa6d-bfadc2a7eb43
dadf8743-782b-4db0-b3d9-636b08232a8e	500	2026-01-26 22:35:35.482624	2026-01-26 22:35:35.482624	e72c0140-5e78-4e29-9292-52406f7c300b	75f3a436-82d2-4933-a44b-7d5623f1875a
dd8fccf1-1d6a-4380-a692-ff07bb031f52	200	2026-01-26 22:36:05.904498	2026-01-26 22:36:05.904498	e72c0140-5e78-4e29-9292-52406f7c300b	dbc6e90d-ab71-4eca-bc6d-313e42359bf6
840045d1-a8f1-414a-9c9f-99e0a85b5129	400	2026-01-26 22:41:44.421881	2026-01-26 22:41:44.421881	e72c0140-5e78-4e29-9292-52406f7c300b	83174eef-4ccb-43be-8b03-acd8809503e7
73bdde40-5b8d-42f9-9490-d4e4239fa9d8	400	2026-01-26 22:43:59.768896	2026-01-26 22:43:59.768896	e72c0140-5e78-4e29-9292-52406f7c300b	cfcf37ef-1902-485a-8e27-dd297660d37b
ecaf804f-2282-4c27-a260-cd49242c3dd2	300	2026-01-26 22:45:27.603141	2026-01-26 22:45:27.603141	e72c0140-5e78-4e29-9292-52406f7c300b	dd620cda-5dc4-4891-bf26-912fdf2018e3
88a3e06b-ee85-4b5d-9df4-faf5ff077854	300	2026-01-26 22:46:20.169519	2026-01-26 22:46:20.169519	e72c0140-5e78-4e29-9292-52406f7c300b	91fd0b37-8b45-4913-9125-34dc80830ffc
21516867-f8fc-4956-9fda-e2b2f4e6d6c7	400	2026-01-26 22:47:05.630729	2026-01-26 22:47:05.630729	e72c0140-5e78-4e29-9292-52406f7c300b	0981584c-b590-4211-913a-2bb73fb5a300
715c440e-590b-4216-b995-7c649935860e	200	2026-01-26 22:47:37.487832	2026-01-26 22:47:37.487832	e72c0140-5e78-4e29-9292-52406f7c300b	85843ca5-3709-4f96-b4c3-c709b737cfa1
1a99d263-064e-48e6-bb3a-151a05a17319	500	2026-01-26 22:48:18.808357	2026-01-26 22:48:18.808357	e72c0140-5e78-4e29-9292-52406f7c300b	9315b17b-ea85-46f0-8c8b-cbb65a17fa9b
5408aab4-702f-4c75-b6a4-5edb75c926f4	400	2026-01-26 22:48:58.86623	2026-01-26 22:48:58.86623	e72c0140-5e78-4e29-9292-52406f7c300b	b03a02da-4588-4bf4-85dc-1086583fabdd
ec98b986-4e04-44ab-8621-99f8cc51a9d5	3000	2026-01-26 22:49:38.226033	2026-01-26 22:49:38.226033	e72c0140-5e78-4e29-9292-52406f7c300b	c007a0e1-a14c-4cfb-bdca-616c3fcf8bbd
3bd18c05-ce41-4313-9aaf-4012eb06f073	200	2026-01-26 22:50:20.523584	2026-01-26 22:50:20.523584	e72c0140-5e78-4e29-9292-52406f7c300b	28520c25-50a5-424a-94a2-21afe5456b8d
90b6a2fa-17d1-49f7-839f-ed05fb798030	200	2026-01-26 22:51:05.355384	2026-01-26 22:51:05.355384	e72c0140-5e78-4e29-9292-52406f7c300b	e91999d9-ab09-4217-b703-07e3e4b6e490
124f3e54-9dd0-42c2-b428-d1d59dd82dfa	200	2026-01-26 22:51:59.598227	2026-01-26 22:51:59.598227	e72c0140-5e78-4e29-9292-52406f7c300b	8d563637-f368-49c4-a851-f7ec5e9116ab
211f07bc-9d47-40a6-8877-aa8eacc2d67a	200	2026-01-26 22:52:38.694701	2026-01-26 22:52:38.694701	e72c0140-5e78-4e29-9292-52406f7c300b	10c8a055-1239-485f-8b44-05897d01354a
7f3330cd-9600-4f56-82a7-f3bcb3f5085f	200	2026-01-26 22:55:32.105292	2026-01-26 22:55:32.105292	e72c0140-5e78-4e29-9292-52406f7c300b	9aada6e2-cb21-463c-964d-30d60dcd996d
441df906-230d-4a24-9842-9acb58acd340	200	2026-01-26 22:56:06.290245	2026-01-26 22:56:06.290245	e72c0140-5e78-4e29-9292-52406f7c300b	e1a48126-7589-45df-8c56-b1b21bf58e4c
acbb720b-9d82-472d-8baa-8ea653270765	200	2026-01-26 22:58:06.923304	2026-01-26 22:58:06.923304	e72c0140-5e78-4e29-9292-52406f7c300b	fdb70e6e-63a3-4ef4-8d38-982fd4192f34
aef6061f-2872-453e-ab40-0a642a0444bb	300	2026-01-26 22:58:46.596474	2026-01-26 22:58:46.596474	e72c0140-5e78-4e29-9292-52406f7c300b	71501453-4680-480e-a824-1228a19117c1
b0315200-af8e-419d-bbab-6fe96a36a09f	300	2026-01-26 22:59:18.129485	2026-01-26 22:59:18.129485	e72c0140-5e78-4e29-9292-52406f7c300b	dbcc5c89-680b-431c-a047-685b023578ac
db75a4dd-0413-4da5-815d-a4acd99c2962	400	2026-01-26 22:59:51.151338	2026-01-26 22:59:51.151338	e72c0140-5e78-4e29-9292-52406f7c300b	e2498062-282f-423a-bf4b-da537870f326
bc70bfe9-140f-4e5c-82c6-e3babf3c8e23	300	2026-01-26 23:16:03.078023	2026-01-26 23:16:03.078023	e72c0140-5e78-4e29-9292-52406f7c300b	9e50cfae-c5e1-4321-9814-ff7705ca6165
8f32b6fe-c64b-4b1e-98ad-f10b02dc7080	300	2026-01-26 23:16:47.143808	2026-01-26 23:16:47.143808	e72c0140-5e78-4e29-9292-52406f7c300b	2a502d3d-a3d4-4838-b4bf-73a03ebee775
f5ef8021-e7b8-4b6b-a567-fa4deced2d2b	3000	2026-01-26 23:19:00.266687	2026-01-26 23:19:00.266687	e72c0140-5e78-4e29-9292-52406f7c300b	eaa5cfbc-84d5-417d-8e65-8ce4539ed94e
54d4471e-63e8-49a0-9b6a-78c0ca1e6ac5	200	2026-01-26 23:19:46.627612	2026-01-26 23:19:46.627612	e72c0140-5e78-4e29-9292-52406f7c300b	1fe10e2d-2ac9-49a8-9daf-2968a0224cab
eb4d3cfe-110a-4ee1-a1be-f85d81a3c79a	300	2026-01-26 23:20:27.405585	2026-01-26 23:20:27.405585	e72c0140-5e78-4e29-9292-52406f7c300b	ca1d0ac8-2c23-402e-8a0e-0ea67af96b8c
2ed3a6a6-6145-461b-88d1-6e8a884d1e51	300	2026-01-26 23:21:11.952254	2026-01-26 23:21:11.952254	e72c0140-5e78-4e29-9292-52406f7c300b	0e56adb6-010f-46eb-8b18-454f9b72195e
f4c4ab97-b7fe-4303-917a-6150ce7470ad	400	2026-01-26 23:21:59.723252	2026-01-26 23:21:59.723252	e72c0140-5e78-4e29-9292-52406f7c300b	7a4ada46-f8fb-453b-a3df-d24a81466a30
bc6a4d83-e456-463c-aeec-3453efa504a8	300	2026-01-26 23:23:05.234249	2026-01-26 23:23:05.234249	e72c0140-5e78-4e29-9292-52406f7c300b	a68e5b58-e2b1-40fb-998c-c7100ce6a0cf
5a488e47-aa2b-44e0-a417-d062b68cf45c	2000	2026-01-26 23:23:55.071824	2026-01-26 23:23:55.071824	e72c0140-5e78-4e29-9292-52406f7c300b	47f10a99-b798-4a25-92de-cfb62f91cdef
1ea73817-3531-4b7d-aee7-5d3aba39ed23	500	2026-01-26 23:24:44.361815	2026-01-26 23:24:44.361815	e72c0140-5e78-4e29-9292-52406f7c300b	aa7b0400-9511-42d7-b433-aa6b16b34c0b
9f0b1e7e-52e2-4ec9-8161-144ae64e98a7	500	2026-01-26 23:25:40.766709	2026-01-26 23:25:40.766709	e72c0140-5e78-4e29-9292-52406f7c300b	9746bbef-e8e0-4f54-9ca7-bceecff68882
21c36304-d108-46c5-b0c2-5a6285c59747	600	2026-01-26 23:26:50.166387	2026-01-26 23:26:50.166387	e72c0140-5e78-4e29-9292-52406f7c300b	a3d949d8-5498-403b-a6df-9c218e1d8360
c6c9076f-fd9c-4651-ba3e-1a7fcd450a59	400	2026-01-26 23:28:23.334598	2026-01-26 23:28:23.334598	e72c0140-5e78-4e29-9292-52406f7c300b	5fd01040-f892-4ca6-af48-9879d873878c
376a3726-9046-4736-8f2d-8fece9a431ba	8000	2026-01-26 23:27:43.394902	2026-01-26 23:27:43.394902	e72c0140-5e78-4e29-9292-52406f7c300b	3430251e-238d-4782-b060-fad61b54fe92
18891fc9-48c5-4626-9e00-8e1451146831	200	2026-01-27 09:00:56.458398	2026-01-27 09:00:56.458398	e72c0140-5e78-4e29-9292-52406f7c300b	cc59bdcf-358e-487c-a688-4a3e3aad1ba8
d35ea876-ddd9-4be9-b1d5-75750bded31e	300	2026-01-27 09:01:31.658561	2026-01-27 09:01:31.658561	e72c0140-5e78-4e29-9292-52406f7c300b	7e370397-7ee8-41e1-a958-73d80a9516c3
61416406-f893-4b21-8ee1-a221e7ae736e	300	2026-01-27 09:02:39.079198	2026-01-27 09:02:39.079198	e72c0140-5e78-4e29-9292-52406f7c300b	4b895154-0e3d-4786-8217-efc12a205551
e64bbe32-e22f-4eac-809e-492f2c3d3b61	300	2026-01-27 09:03:15.229819	2026-01-27 09:03:15.23082	e72c0140-5e78-4e29-9292-52406f7c300b	120aca55-4b49-4534-b0fb-011eb95dc726
08788012-1c73-4178-82dd-3778e5bec65e	3000	2026-01-27 09:03:55.432732	2026-01-27 09:03:55.432732	e72c0140-5e78-4e29-9292-52406f7c300b	4ca2cf28-b00c-41e5-8995-6b8fa11dc7e9
1266a505-07b3-482f-b4f4-6bc1a51df941	200	2026-01-27 09:04:50.589756	2026-01-27 09:04:50.589756	e72c0140-5e78-4e29-9292-52406f7c300b	20a6093b-2a69-4b4c-96fb-a8725fb110cc
0df7e5fb-9739-43d8-969f-7f304bde3cd1	300	2026-01-27 09:05:29.346611	2026-01-27 09:05:29.346611	e72c0140-5e78-4e29-9292-52406f7c300b	a603971f-eeb9-4e2f-b4c4-66e54f1d0d82
da9c3bd2-80bf-4a44-b6f1-4d73e8338905	300	2026-01-27 09:06:12.266002	2026-01-27 09:06:12.266002	e72c0140-5e78-4e29-9292-52406f7c300b	214ed421-6175-4eb2-bc4f-2f86dbf2f91f
918e1ecf-2360-4643-83f0-5d44544a1b97	300	2026-01-27 09:06:44.939295	2026-01-27 09:06:44.939295	e72c0140-5e78-4e29-9292-52406f7c300b	dd71a728-97c7-46fb-b6dd-33b3ef03718b
9fdc4be9-d59b-4f7e-bc5f-da4296fa81c9	200	2026-01-27 09:07:11.403291	2026-01-27 09:07:11.403291	e72c0140-5e78-4e29-9292-52406f7c300b	e4b1719b-17f5-4f5c-aee1-f1dad458a73e
4391b6fd-55ca-4879-9b90-881bc61365fe	400	2026-01-27 09:07:38.691671	2026-01-27 09:07:38.691671	e72c0140-5e78-4e29-9292-52406f7c300b	bdfe7eb2-63de-42d3-a743-1c4011c1d27d
7c9a32b4-0796-4139-b143-6d6d1e847423	300	2026-01-27 09:08:05.844193	2026-01-27 09:08:05.844193	e72c0140-5e78-4e29-9292-52406f7c300b	50e96829-c5ce-4e45-a29b-d77720426d1f
1d67291e-a434-4f53-bfc7-c09c98e7774d	400	2026-01-27 09:11:41.135848	2026-01-27 09:11:41.135848	e72c0140-5e78-4e29-9292-52406f7c300b	802cd111-e7bc-4450-8917-277259f014ee
a0f6372e-a10c-4bea-8ae8-595eb3f092c3	300	2026-01-27 09:12:39.184143	2026-01-27 09:12:39.184143	e72c0140-5e78-4e29-9292-52406f7c300b	2a20b470-c5bd-4f22-8a08-cf2b91050bc3
b0fa3ba7-1755-4b98-b66f-3d923a52716b	300	2026-01-27 09:13:13.15636	2026-01-27 09:13:13.15636	e72c0140-5e78-4e29-9292-52406f7c300b	189adc1b-90f6-4be1-991a-f18e6469aa31
6af86de1-07a2-4c06-842b-9864d6aee057	300	2026-01-27 09:13:43.516061	2026-01-27 09:13:43.516061	e72c0140-5e78-4e29-9292-52406f7c300b	285bdc01-f1c3-4927-b591-6b90728d76b9
cf5c5f94-8bfc-4b1a-a5a1-5f0ac5c57e1f	300	2026-01-27 09:14:25.807845	2026-01-27 09:14:25.807845	e72c0140-5e78-4e29-9292-52406f7c300b	772fe4a0-f2f5-4363-aa36-7ee4720fd43c
e8d6bcdd-404b-4f03-a525-4b99b47535b7	400	2026-01-27 09:14:57.814682	2026-01-27 09:14:57.814682	e72c0140-5e78-4e29-9292-52406f7c300b	dce4ca49-9d79-4802-a272-3dff056d82b9
d0704cdd-5ae3-44b3-bae6-a1c9fbbd554b	200	2026-01-27 09:15:36.465641	2026-01-27 09:15:36.465641	e72c0140-5e78-4e29-9292-52406f7c300b	e1df13f9-cd23-427c-a4ad-0ff0d7ca1a89
6ead5857-e008-4c3b-bdff-ee3488e2b9c7	300	2026-01-27 09:16:26.18862	2026-01-27 09:16:26.18862	e72c0140-5e78-4e29-9292-52406f7c300b	37c01067-97d2-405f-84a9-cf50de299aec
45726f52-3953-4bc2-b6b1-17c3cb69d6bf	200	2026-01-27 09:17:02.239917	2026-01-27 09:17:02.239917	e72c0140-5e78-4e29-9292-52406f7c300b	03485363-a8eb-43d3-b904-0599d1bf7492
90f60230-6f3d-4d95-9e1e-ded7eb6b09e1	200	2026-01-27 09:17:45.928252	2026-01-27 09:17:45.928252	e72c0140-5e78-4e29-9292-52406f7c300b	a781f555-95cd-4e43-bbef-99389293e514
a2b6f945-893b-4a59-ad0a-978729528712	200	2026-01-27 09:18:13.589544	2026-01-27 09:18:13.589544	e72c0140-5e78-4e29-9292-52406f7c300b	fd481ab7-f804-4ecf-a683-a21f0c740043
e92b7e15-50df-4f9f-8d1f-1fd848dee772	200	2026-01-27 09:18:45.412918	2026-01-27 09:18:45.412918	e72c0140-5e78-4e29-9292-52406f7c300b	2f084dfc-6285-4246-a08e-f5bd7665c2a6
4cd2a0d6-c918-4deb-a45d-f7302bdce9c4	200	2026-01-27 09:19:28.00615	2026-01-27 09:19:28.00615	e72c0140-5e78-4e29-9292-52406f7c300b	7b8caaf3-7e5b-4b59-9a4e-8efffaafb5ac
3d5516d6-b772-41e4-b06e-605f3edaac9a	300	2026-01-27 09:20:09.441436	2026-01-27 09:20:09.441436	e72c0140-5e78-4e29-9292-52406f7c300b	0097b421-5403-4e0b-a8ec-69a7761fd044
f6f6ff75-900c-4e67-8f26-6d69ef9eb37c	200	2026-01-27 09:20:39.243625	2026-01-27 09:20:39.243625	e72c0140-5e78-4e29-9292-52406f7c300b	6856c8dd-1cb7-40d7-8ffe-bdbcbedcf416
d462bad7-f643-4c02-ae15-ef57bac0f226	200	2026-01-27 09:21:05.336294	2026-01-27 09:21:05.336294	e72c0140-5e78-4e29-9292-52406f7c300b	23d808fc-8f8e-4152-a512-9244bf56cf99
11368132-5102-4354-8632-5b9a8f59c648	200	2026-01-27 09:21:49.645775	2026-01-27 09:21:49.645775	e72c0140-5e78-4e29-9292-52406f7c300b	60725f5d-5a8c-424a-8d8e-241040bda667
d8279376-ff94-497f-8883-d5e524724393	300	2026-01-27 09:26:17.845338	2026-01-27 09:26:17.845338	e72c0140-5e78-4e29-9292-52406f7c300b	6a7ee9fe-8a4b-4971-9604-c6d448170d0b
95f40e74-041e-4165-b028-b0450326a248	200	2026-01-27 09:26:52.621408	2026-01-27 09:26:52.621408	e72c0140-5e78-4e29-9292-52406f7c300b	f81d04d6-8c05-4f73-8641-ca398615d2f6
052975a4-9091-40d6-888f-dccf9ef9fb2c	200	2026-01-27 09:27:28.182996	2026-01-27 09:27:28.182996	e72c0140-5e78-4e29-9292-52406f7c300b	45dde3c5-6de6-48b6-aec5-32fead5008a3
67a49c92-4efc-4ac6-8c88-ff37ea308183	200	2026-01-27 09:27:58.775307	2026-01-27 09:27:58.775307	e72c0140-5e78-4e29-9292-52406f7c300b	98c1b464-e1b5-40d1-b3e4-fdc95771d2fe
850533aa-134f-43d8-9e42-73525a32be4d	200	2026-01-27 09:28:37.211244	2026-01-27 09:28:37.211244	e72c0140-5e78-4e29-9292-52406f7c300b	1f833e27-a4a0-4af2-8e94-5538e8ec1e18
d45404d2-625d-4772-99d1-69a596811a8c	200	2026-01-27 09:29:16.268373	2026-01-27 09:29:16.268373	e72c0140-5e78-4e29-9292-52406f7c300b	8f6aeebb-67a0-484e-a643-71dc89a5f9b7
6129c13d-d428-43ce-bdff-1eb2338a1820	200	2026-01-27 09:30:06.409092	2026-01-27 09:30:06.409092	e72c0140-5e78-4e29-9292-52406f7c300b	2dcafc1f-e9de-4b1b-878d-ed7f327dc99e
7aac2acd-d3f3-4633-be1b-482ef7607964	200	2026-01-27 09:30:43.891845	2026-01-27 09:30:43.891845	e72c0140-5e78-4e29-9292-52406f7c300b	f3185df8-bda7-483d-9845-d1d916edae87
67886a67-283f-4703-a983-ebd0a7d13542	200	2026-01-27 09:31:45.184028	2026-01-27 09:31:45.184028	e72c0140-5e78-4e29-9292-52406f7c300b	e4d49eab-07bc-4952-91e7-4d4df0e96e76
d437b883-d066-4e53-93e2-401b4462f393	200	2026-01-27 09:32:13.66004	2026-01-27 09:32:13.66004	e72c0140-5e78-4e29-9292-52406f7c300b	5d76c756-01a4-4b76-b0c6-1e13d3bfb751
11fab668-f831-4c73-a89a-52be4af0489c	200	2026-01-27 09:32:46.308486	2026-01-27 09:32:46.308486	e72c0140-5e78-4e29-9292-52406f7c300b	ebe20932-f21a-402c-85f3-a86b8bb52ef2
41f9e8f3-70dc-4ebd-9fc0-2beba9ae1025	300	2026-01-27 09:33:17.875415	2026-01-27 09:33:17.875415	e72c0140-5e78-4e29-9292-52406f7c300b	2f1a533e-d36f-4be1-a383-b263da195104
0ab11b98-f18b-4d50-b779-ee39ac4740a2	200	2026-01-27 09:33:52.641935	2026-01-27 09:33:52.641935	e72c0140-5e78-4e29-9292-52406f7c300b	610dc812-3e6f-4c86-b699-a07e4f438edd
32c9c9b5-2594-468e-9b5b-89df455232f2	200	2026-01-27 09:34:17.054959	2026-01-27 09:34:17.054959	e72c0140-5e78-4e29-9292-52406f7c300b	038ecca3-1d55-4e68-9054-78b3950fc8bb
0249ead2-1772-41f9-9250-254888c1991d	200	2026-01-27 09:40:39.850563	2026-01-27 09:40:39.850563	e72c0140-5e78-4e29-9292-52406f7c300b	e8d6cd54-6526-44da-9b37-6b12cf9e0c98
68f62c0d-6999-4847-9532-243d3d0525b3	200	2026-01-27 09:41:16.291419	2026-01-27 09:41:16.291419	e72c0140-5e78-4e29-9292-52406f7c300b	3c22e63c-40a1-4aba-ad82-178f13697461
08e75aa0-96ab-4208-a9b0-cf099a68c9b2	200	2026-01-27 09:41:48.394987	2026-01-27 09:41:48.394987	e72c0140-5e78-4e29-9292-52406f7c300b	161b72c5-c773-4e80-a7d0-f9a98f33247a
c70989fe-9dca-48a5-96c8-b3c9ef777e2b	200	2026-01-27 09:42:23.285042	2026-01-27 09:42:23.285042	e72c0140-5e78-4e29-9292-52406f7c300b	a279bfd5-584e-4658-b382-037543618264
481de9c3-2ed7-467b-9562-93066be02e6f	500	2026-01-27 09:42:54.001922	2026-01-27 09:42:54.001922	e72c0140-5e78-4e29-9292-52406f7c300b	4fa87a14-3741-459c-b020-25c89425838a
c4289a24-a013-409f-b6bf-61cfa3e3e2c3	3000	2026-01-27 09:43:26.16414	2026-01-27 09:43:26.16414	e72c0140-5e78-4e29-9292-52406f7c300b	c3c56cc8-2307-4f7e-841c-ebb8f09c114c
6f51f002-d732-465a-a794-2271ef55919a	300	2026-01-27 09:43:51.732274	2026-01-27 09:43:51.732274	e72c0140-5e78-4e29-9292-52406f7c300b	29529a6d-d69f-4dba-8568-655e1d1b35c8
57b38f49-6ac5-4d3e-9407-baa0b371b8ef	300	2026-01-27 09:44:17.997621	2026-01-27 09:44:17.997621	e72c0140-5e78-4e29-9292-52406f7c300b	6e5e7cf6-71a2-4078-968e-93fb1b40aa9c
e6d3af7b-4c14-43fd-96dd-c63f29ed2174	500	2026-01-27 09:49:24.260508	2026-01-27 09:49:24.260508	e72c0140-5e78-4e29-9292-52406f7c300b	7256355d-3afb-4677-b680-0237114987c9
a003f03f-a083-40c4-81de-32e55f972903	300	2026-01-27 09:49:58.913055	2026-01-27 09:49:58.913055	e72c0140-5e78-4e29-9292-52406f7c300b	5e2c12f0-e141-444e-8365-763135335af7
6edcf778-6f7b-4436-820f-5901464e7b52	300	2026-01-27 09:50:36.941458	2026-01-27 09:50:36.941458	e72c0140-5e78-4e29-9292-52406f7c300b	35298b94-9699-4d03-a429-b8447dfe2f07
3e2df7b5-c801-4031-8317-69cdbf9cb195	200	2026-01-27 09:51:15.584833	2026-01-27 09:51:15.584833	e72c0140-5e78-4e29-9292-52406f7c300b	0858eb59-41d7-4828-8174-7709b36a91fb
5daa77c5-3dd1-4d0f-8c44-aec5f87c70d0	300	2026-01-27 09:51:43.930033	2026-01-27 09:51:43.930033	e72c0140-5e78-4e29-9292-52406f7c300b	f280ab86-5bdc-43d0-b095-3394eeb7e3c8
3b22906f-f83a-42d0-a92c-c20201f563f8	400	2026-01-27 09:52:32.161971	2026-01-27 09:52:32.161971	e72c0140-5e78-4e29-9292-52406f7c300b	72fe80e2-174b-48f8-a307-ae74c6ff1e87
7e6f8aa0-44f4-4e83-88e8-6258b2b7e671	300	2026-01-27 09:53:02.176689	2026-01-27 09:53:02.176689	e72c0140-5e78-4e29-9292-52406f7c300b	cecdc323-40dc-42aa-969b-78d84320098c
4e991d9b-2480-49a7-9061-55be8fd1a020	300	2026-01-27 09:53:31.834116	2026-01-27 09:53:31.834116	e72c0140-5e78-4e29-9292-52406f7c300b	12e765f2-8c69-4cef-886b-b0a867b79ec8
fe124e16-b82f-4d78-9a1f-08607135bf63	200	2026-01-27 09:54:00.454882	2026-01-27 09:54:00.454882	e72c0140-5e78-4e29-9292-52406f7c300b	7cbb5cf9-67c2-4e8e-ad91-b7c59220d1f8
d6352e74-febe-46e4-812f-8a2c4673fab4	300	2026-01-27 09:54:25.327697	2026-01-27 09:54:25.327697	e72c0140-5e78-4e29-9292-52406f7c300b	c5a72793-9023-4d29-8a9a-56a37b3412da
173d2544-7898-4a88-a2be-1b9bce40ee5a	200	2026-01-27 09:54:53.467763	2026-01-27 09:54:53.467763	e72c0140-5e78-4e29-9292-52406f7c300b	6f015b87-3087-47e6-8919-c5cd3a466126
3b2ee921-a96e-45a3-a0f5-516ed930a186	200	2026-01-27 09:55:30.054361	2026-01-27 09:55:30.054361	e72c0140-5e78-4e29-9292-52406f7c300b	510ecdee-f493-4e98-9dae-a6678b90b799
98fe368a-29a5-43a1-9e09-d0b61547e3cb	300	2026-01-27 09:55:57.957593	2026-01-27 09:55:57.957593	e72c0140-5e78-4e29-9292-52406f7c300b	1fdc92f3-8e12-4cc6-bf29-6daf73757477
481aa2aa-4709-4c0f-9d8d-75599add5c4d	200	2026-01-27 09:59:39.18802	2026-01-27 09:59:39.18802	e72c0140-5e78-4e29-9292-52406f7c300b	a7957b0f-359f-4fa1-ba2b-09edc83676e2
3fbaa667-e1a4-4547-af3c-80e39247e1a8	200	2026-01-27 10:00:05.438048	2026-01-27 10:00:05.438048	e72c0140-5e78-4e29-9292-52406f7c300b	eec05e51-9610-4952-b17f-a4fc52b0a812
9846fe53-5a70-434a-8396-e55330dc7589	200	2026-01-27 10:00:34.162893	2026-01-27 10:00:34.162893	e72c0140-5e78-4e29-9292-52406f7c300b	75b3ab93-ab11-4a4f-b2e5-adda2d2085f0
09b9a426-9aa0-4b5e-9611-dc0a17022e04	200	2026-01-27 10:01:06.325976	2026-01-27 10:01:06.325976	e72c0140-5e78-4e29-9292-52406f7c300b	e90fbca4-0f86-41f0-8502-dce905da33b4
b87eb0f8-3d50-4bab-924e-f0c7f246f23e	300	2026-01-27 10:01:31.904785	2026-01-27 10:01:31.904785	e72c0140-5e78-4e29-9292-52406f7c300b	e03f4312-3892-4286-a78b-c29b07173394
0b9dc527-03a0-4031-b1b1-c59a4d499d47	300	2026-01-27 10:02:08.943797	2026-01-27 10:02:08.943797	e72c0140-5e78-4e29-9292-52406f7c300b	307fa15b-0ad5-4484-b9be-e150b6b7bf28
d01e5a12-50a0-49b1-bc1d-caaa7c38468a	500	2026-01-27 10:02:46.681713	2026-01-27 10:02:46.681713	e72c0140-5e78-4e29-9292-52406f7c300b	870f7938-0606-4736-a7d0-f2b2768e12cf
55bf2abc-5891-4635-bd86-cf1ed8700d17	200	2026-01-27 10:03:26.254026	2026-01-27 10:03:26.254026	e72c0140-5e78-4e29-9292-52406f7c300b	b219eefd-292b-4950-ba07-ad7648f4204e
33a3f003-6f96-42c6-b04c-4ccda940a21c	300	2026-01-27 10:03:55.703338	2026-01-27 10:03:55.703338	e72c0140-5e78-4e29-9292-52406f7c300b	ac261fac-5416-4838-ba08-0eff8e8a627f
d2697efb-ff04-441e-b36e-a8e8442c2413	300	2026-01-27 10:04:42.877895	2026-01-27 10:04:42.877895	e72c0140-5e78-4e29-9292-52406f7c300b	5edfd9dd-38de-468e-8ef1-d3f1041a86dc
a13a7d03-106d-43f3-8639-bf18dd6f3272	200	2026-01-27 10:05:11.132318	2026-01-27 10:05:11.132318	e72c0140-5e78-4e29-9292-52406f7c300b	9a1f0fce-6082-49e1-b060-4ab6f086a65d
bba0f1d5-78d5-4e49-a8ee-6bf69a4a9eb3	200	2026-01-27 10:05:39.770936	2026-01-27 10:05:39.770936	e72c0140-5e78-4e29-9292-52406f7c300b	759119d9-90c6-4b2d-9c4a-c5cd81b5f41f
\.


--
-- Data for Name: farm_types; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.farm_types (id, created_at, description, is_active, type_name, updated_at, created_by, updated_by) FROM stdin;
b48bd8af-fece-45fd-be53-701d7631cbd4	2025-10-29 11:36:01.213318	Farms primarily focused on milk production from dairy cattle or buffalo	t	Dairy	\N	\N	\N
0949df59-9562-48c1-aba8-87460e62aa27	2025-10-29 11:36:01.213318	Farms focused on raising cattle for meat production	t	Beef	\N	\N	\N
86b9c215-fe06-491a-bc09-5a4f37a47f11	2025-10-29 11:36:01.213318	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat	t	Poultry	\N	\N	\N
9ec06a18-ebbc-4984-a354-4b965bda31d7	2025-10-29 11:36:01.213318	Farms with multiple types of livestock (cattle, poultry, goats, etc.)	t	Mixed	\N	\N	\N
f37fafd8-8a7b-40a8-9c1b-75df8cca3ded	2025-10-29 11:36:01.213318	Farms raising goats for milk, meat, or fiber production	t	Goat	\N	\N	\N
1f4b5386-2538-4375-9de9-79abc5e0a4a9	2025-10-29 11:36:01.213318	Farms focused on pig/swine production for meat	t	Pig	\N	\N	\N
95c42c08-7eed-45d8-a920-1ac4f0faa765	2025-10-29 11:36:01.213318	Farms specializing in sheep for wool, meat, or milk production	t	Sheep	2025-11-27 10:12:26.71466	\N	\N
2b358d7c-31e1-4b73-9567-f7873c769d07	2026-01-26 13:56:36.743871	Fish and aquatic animal farming operations	t	Aquaculture	\N	\N	\N
bb29b117-092e-4ed4-841d-5272db13cec5	2026-01-26 13:56:36.743871	Farms for breeding, training, or maintaining horses	t	Horse	\N	\N	\N
c3912cb8-90f3-4863-8595-bb47793ae2bb	2026-01-26 13:56:36.743871	Farms raising rabbits for meat or fur	t	Rabbit	\N	\N	\N
2195930d-74b7-46eb-94ce-911c540930b8	2026-01-26 13:56:36.743871	Bee farms for honey and pollination services	t	Apiary	\N	\N	\N
f77aa43e-b401-4a8f-802a-f3148ec78bcd	2026-01-26 13:56:36.743871	Other types of livestock or specialty farming operations	t	Other	\N	\N	\N
\.


--
-- Data for Name: farms; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.farms (id, address, created_at, district, farm_name, gps_latitude, gps_longitude, is_active, owner_contact, owner_name, province, total_animals, updated_at, created_by, farm_type_id, updated_by, description) FROM stdin;
ef243267-d147-4784-aea2-15ce4fa1871b	Akurana, Sri Lanka	2026-01-26 15:10:45.278232	MATALE	Akurana	7.36440710	80.61883220	t	\N	Sunil	CENTRAL	200	2026-01-26 15:10:45.301712	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
db399a82-f283-478b-af2a-b2fff0f5d5c5	Doluwa, Sri Lanka	2026-01-26 15:11:57.64996	KANDY	Doluwa	7.18618730	80.60632530	t	\N	Sunil	CENTRAL	200	2026-01-26 15:11:57.657574	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ab32e8c3-9678-45c8-a163-00fb13244f40	Ganga Ihala Korale, Sri Lanka	2026-01-26 15:13:38.947189	KANDY	Ganagalhala Korale	7.13254710	80.56837700	t	\N	Sunil	CENTRAL	200	2026-01-26 15:13:38.953674	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
cb09826e-c389-4df7-a1f3-389742b34a51	Kandy Four Gravets & Gangawata Korale, Sri Lanka	2026-01-26 15:14:24.698042	KANDY	Ganagawata Korale	7.29316810	80.63501050	t	\N	Sunil	CENTRAL	200	2026-01-26 15:14:24.703154	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e261d0a7-1be9-4f60-8d1a-3c4c4be46332	Galagedara, Sri Lanka	2026-01-26 15:15:29.784417	KANDY	Galagedara	7.36996830	80.53273240	t	\N	Sunil	CENTRAL	200	2026-01-26 15:15:29.78646	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
751e611c-3d91-4f68-bff1-0e233892c273	Galaha, Sri Lanka	2026-01-26 15:16:07.745317	KANDY	Galaha	7.19684440	80.67153170	t	\N	Sunil	CENTRAL	200	2026-01-26 15:16:07.75185	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
57976916-ef68-4321-ae0d-7374bde3d8c4	Gampola, Sri Lanka	2026-01-26 15:16:52.288138	KANDY	Gampola	7.16354340	80.57024430	t	\N	Sunil	CENTRAL	200	2026-01-26 15:16:52.298207	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
54ecb014-80f6-417f-9cf4-60720e3c142e	Harispattuwa, Sri Lanka	2026-01-26 15:17:44.056543	KANDY	Harispattuwa	7.32803720	80.58749900	t	\N	Sunil	CENTRAL	200	2026-01-26 15:17:44.06012	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
fcf6aee2-50ee-496a-8c32-5902d595437e	Hasalaka, Sri Lanka	2026-01-26 15:18:29.151381	KANDY	Hasalaka	7.35732650	80.95394750	t	\N	Sunil	CENTRAL	200	2026-01-26 15:18:29.158341	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
77c4151e-555c-492b-8753-ebeeb004b63f	Hatharaliyadda, Sri Lanka	2026-01-26 15:19:09.604509	KANDY	Hatharaliyadda	7.33577980	80.47115370	t	\N	Sunil	CENTRAL	200	2026-01-26 15:19:09.610728	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a6fc7e29-fde6-4372-8973-74d97b91ebd3	Kundasale, Sri Lanka	2026-01-26 15:19:43.813193	KANDY	Kundasale	7.28086810	80.68413080	t	\N	Sunil	CENTRAL	200	2026-01-26 15:19:43.82047	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
289a78d0-e5f7-4f8f-8479-47cebb34ce5c	Nawalapitiya, Sri Lanka	2026-01-26 15:20:24.376487	KANDY	Nawalapitiya	7.01815610	80.49278260	t	\N	Sunil	CENTRAL	200	2026-01-26 15:20:24.384134	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e9edf87b-6ad7-42e8-9259-b87f36557541	Panwila, Sri Lanka	2026-01-26 15:20:55.007761	KANDY	Panvila	7.36362350	80.71565410	t	\N	Sunil	CENTRAL	200	2026-01-26 15:20:55.011309	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
6f81d75a-6738-47fb-9cdb-8db80021697d	Poojapitiya, Sri Lanka	2026-01-26 15:21:28.179113	KANDY	Poojapitiya	7.37921310	80.59105670	t	\N	Sunil	CENTRAL	200	2026-01-26 15:21:28.182627	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
aa36f3f9-8c27-4e9b-a9e5-c3ebb5a35559	Pussellawa, Sri Lanka	2026-01-26 15:22:05.904455	KANDY	Pussellawa	7.11098270	80.63824130	t	\N	Sunil	CENTRAL	200	2026-01-26 15:22:05.910637	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a4abe78e-952e-4758-bbe5-4ff78ea50b60	Thalathuoya, Sri Lanka	2026-01-26 15:22:38.765593	KANDY	Thalathuoya	7.24776190	80.68539860	t	\N	Sunil	CENTRAL	200	2026-01-26 15:22:38.76964	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
56f34b3d-103c-4259-b0d8-d74689435237	Teldeniya, Sri Lanka	2026-01-26 15:23:18.555994	KANDY	Teldeniya	7.31538190	80.74493090	t	\N	Sunil	CENTRAL	200	2026-01-26 15:23:18.55953	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
043fc85e-4b96-41a3-aee4-6b2b8e42b9e2	Udadumbara, Sri Lanka	2026-01-26 15:24:12.649278	KANDY	Udadumbara	7.31438630	80.87790380	t	\N	Sunil	CENTRAL	200	2026-01-26 15:24:12.653343	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
d7934555-4b51-45cd-9671-9fbb7f80890c	Udunuwara, Sri Lanka	2026-01-26 15:24:47.261468	KANDY	Udunuwara	7.21488410	80.57101610	t	\N	Sunil	CENTRAL	200	2026-01-26 15:24:47.268468	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e036fc45-3c0b-42cd-bb34-26c7a2768da4	Wattegama, Sri Lanka	2026-01-26 15:25:18.717427	KANDY	Wattegama	7.34914640	80.68539860	t	\N	Sunil	CENTRAL	200	2026-01-26 15:25:18.721487	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
375b250f-71ba-41e4-ae2a-9ebc1c7d3da9	Yatinuwara, Sri Lanka	2026-01-26 15:25:57.149503	KANDY	Yatinuwara	7.28138280	80.54090230	t	\N	Sunil	CENTRAL	200	2026-01-26 15:25:57.152031	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ebcb42d8-cd44-4ef4-8cb5-e570ec75cebe	Ambanganga Korale, Sri Lanka	2026-01-26 16:22:28.651865	MATALE	Ambanganga Korale	7.56509770	80.67424170	t	\N	Sunil	CENTRAL	200	2026-01-26 16:22:50.79755	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	5	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
0426d7c5-9cb9-44b6-92db-1423fa063ed1	Matale, Sri Lanka	2026-01-26 16:25:24.340132	MATALE	Matale	7.46746500	80.62341610	t	\N	Sunil	CENTRAL	200	2026-01-26 16:25:24.347022	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
c84ae897-1b68-48e0-8c32-84504f8b89e8	Naula, Sri Lanka	2026-01-26 16:26:03.104323	MATALE	Naula	7.70720480	80.65359430	t	\N	Sunil	CENTRAL	400	2026-01-26 16:36:00.35199	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	5	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ded4619c-e827-4d71-8403-438b675e970d	Dambulla, Sri Lanka	2026-01-26 16:23:27.783865	MATALE	Dambulla	7.87410170	80.65108560	t	\N	Sunil	CENTRAL	200	2026-01-26 16:24:19.114497	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	5	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
740f9ffa-2678-4bfe-8056-67b661646e5d	Laggala, Sri Lanka	2026-01-26 16:24:48.707114	MATALE	Laggala	7.55152310	80.77132100	t	\N	Sunil	CENTRAL	200	2026-01-26 16:24:48.712063	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
c217e01f-1a79-4952-bdd6-29d111a2a48d	Pallepola, Sri Lanka	2026-01-26 16:26:52.038575	MATALE	Pallepola	7.62329910	80.60653380	t	\N	Sunil	CENTRAL	200	2026-01-26 16:26:52.043999	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
694e98c6-ea1b-42d6-b755-74d0b2861827	Rattota, Sri Lanka	2026-01-26 16:27:24.033999	MATALE	Rattota	7.51979100	80.67694760	t	\N	Sunil	CENTRAL	200	2026-01-26 16:27:24.041238	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a0e29bb7-0c3c-437c-a542-45c6fafe089d	Ukuwela, Sri Lanka	2026-01-26 16:27:51.962185	MATALE	Ukuwela	7.42124820	80.63337360	t	\N	Sunil	CENTRAL	200	2026-01-26 16:27:51.969118	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e4faa691-d0ba-4942-b123-a4dac4cf936c	Wilgamuwa, Sri Lanka	2026-01-26 16:28:26.1736	MATALE	Wilgamuwa	7.51801380	80.95326470	t	\N	Sunil	CENTRAL	200	2026-01-26 16:28:26.177151	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
3fbded90-988d-4ceb-8523-ab36cc1ab5bd	Agarapathana, Sri Lanka	2026-01-26 16:38:11.958243	NUWARA_ELIYA	Agarapathana	6.82041220	80.71035320	t	\N	Sunil	CENTRAL	200	2026-01-26 16:38:11.96425	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e6c2115f-6339-480c-ac11-1e3fb8178ca1	Yatawatta, Sri Lanka	2026-01-26 16:28:56.727129	MATALE	Yatawatta	7.56540900	80.58110450	t	\N	Sunil	CENTRAL	200	2026-01-26 16:28:56.736897	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
f7a3b47a-53a6-4e39-a3c5-00ce53d5f1fc	Galewela, Sri Lanka	2026-01-26 16:24:05.690816	MATALE	Galewala	7.75615720	80.56799860	t	\N	Sunil	CENTRAL	400	2026-01-26 16:32:08.229784	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	5	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
6970219f-30b0-4c5f-99f5-b6a0606e6e62	Bogawantalawa, Sri Lanka	2026-01-26 16:38:59.248726	NUWARA_ELIYA	Bogawanthalawa	6.79723370	80.67569200	t	\N	Sunil	CENTRAL	200	2026-01-26 16:38:59.252722	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2a4baeec-cfd7-4d4a-8174-9218b576c7b3	Ginigathhena, Sri Lanka	2026-01-26 16:39:37.968601	NUWARA_ELIYA	Ginigathhena	6.98931710	80.49269330	t	\N	Sunil	CENTRAL	200	2026-01-26 16:39:50.651096	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	5	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
d4b09b0c-e306-48a9-a4aa-32e85828a9b8	Maskeliya, Sri Lanka	2026-01-26 16:41:50.642876	NUWARA_ELIYA	Maskeliya	6.83292940	80.57092560	t	\N	Sunil	CENTRAL	200	2026-01-26 16:41:50.647391	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
0d477214-1ac7-4aa9-b75e-c02410330c19	Nuwara Eliya, Sri Lanka	2026-01-26 16:42:26.79944	NUWARA_ELIYA	Nuwara Eliya	6.96068860	80.76929590	t	\N	Sunil	CENTRAL	200	2026-01-26 16:42:26.804072	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
fc9f2b4e-ce73-401c-9f92-e15673bff90a	Pundaluoya, Sri Lanka	2026-01-26 16:44:11.237166	NUWARA_ELIYA	Pundaluoya	7.01312780	80.66321030	t	\N	Sunil	CENTRAL	200	2026-01-26 16:44:11.240684	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
4effeb95-3f05-4cec-8d76-fdd4afc7a4e3	Hatton, Sri Lanka	2026-01-26 16:40:42.96699	NUWARA_ELIYA	Hatton	6.90034420	80.59660930	t	\N	Sunil	CENTRAL	200	2026-01-26 16:40:42.973256	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
b79bc8ee-2a84-4b84-a477-33f5dd8e98c4	Ragala, Sri Lanka	2026-01-26 16:45:38.726891	NUWARA_ELIYA	Ragala	7.01117190	80.85560750	t	\N	Sunil	CENTRAL	200	2026-01-26 16:45:38.726891	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
c3b143cd-a142-4996-bfdf-32c54fcaba17	Thalawakele, Sri Lanka	2026-01-26 16:46:54.715499	NUWARA_ELIYA	Thalawakale	6.93337560	80.65766230	t	\N	Sunil	CENTRAL	200	2026-01-26 16:46:54.718707	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e93fc0bb-f560-46a9-add6-a0cd755bd0f7	Kotmale, Sri Lanka	2026-01-26 16:41:21.699973	NUWARA_ELIYA	Kotmale	7.01504580	80.59443870	t	\N	Sunil	CENTRAL	200	2026-01-26 16:41:21.704364	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
c3f894cd-b9d6-4926-b4e0-bb6b0cc65c59	Rikillagaskada, Sri Lanka	2026-01-26 16:46:13.324229	NUWARA_ELIYA	Rikillagaskada	7.14865160	80.79071030	t	\N	Sunil	CENTRAL	200	2026-01-26 16:46:13.330719	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
d092a3a2-aea1-46a0-b9a8-00615764f4d5	Thispane Kanda, Sri Lanka	2026-01-26 16:47:35.111985	NUWARA_ELIYA	Thispane	7.05464000	80.61326460	t	\N	Sunil	CENTRAL	200	2026-01-26 16:47:35.114965	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
d7c3e876-a398-42e2-9b1f-3edf5cda7347	Dehiattakandiya, Sri Lanka	2026-01-26 16:58:47.892085	AMPARA	Dehiattakandiya	7.67119810	81.04653980	t	\N	Nimal	EASTERN	300	2026-01-26 16:58:47.903221	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2b4f99f6-d556-434f-9bd9-5a1c7e33d6da	Addalachchenai, Sri Lanka	2026-01-26 16:59:29.814021	AMPARA	Addalachchenai	7.25067340	81.85435190	t	\N	Nimal	EASTERN	300	2026-01-26 16:59:29.822431	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a390ab6d-99e3-4eb0-815b-3d24099c935e	Akkaraipattu, Sri Lanka	2026-01-26 17:00:09.003429	AMPARA	Akkaraipattu	7.21831950	81.84974350	t	\N	Nimal	EASTERN	300	2026-01-26 17:00:09.018177	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
248d7281-9cb8-425d-aa64-065312c6ec7a	Alayadiwembu, Sri Lanka	2026-01-26 17:01:26.413268	AMPARA	Alayadivembu	7.20227320	81.84980120	t	\N	Nimal	EASTERN	300	2026-01-26 17:01:26.418537	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
aef68083-f999-4c56-827a-cb2e98304b07	Ampara, Sri Lanka	2026-01-26 17:01:59.713274	AMPARA	Ampara	7.29122100	81.67249220	t	\N	Nimal	EASTERN	300	2026-01-26 17:01:59.713274	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
b70b752e-1801-4874-8484-fc739efbca62	Damana, Sri Lanka	2026-01-26 17:02:49.935319	AMPARA	Damana	7.20122070	81.65693000	t	\N	Nimal	EASTERN	300	2026-01-26 17:02:49.940654	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2573afc1-fa8c-490b-86e6-30021afd8023	Dehiattakandiya, Sri Lanka	2026-01-26 17:03:35.482512	AMPARA	Dehiattakandiya	7.67119810	81.04653980	t	\N	Nimal	EASTERN	300	2026-01-26 17:03:35.488549	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
6d57b583-b041-4b3a-9332-520aee8a6db8	Irakkamam, Eragama, Sri Lanka	2026-01-26 17:04:18.846226	AMPARA	Irakkamam	7.24424420	81.74588450	t	\N	Nimal	EASTERN	300	2026-01-26 17:04:18.852455	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
c42837e9-5490-4dcd-86d4-6c6ac0025718	Kalmunai Muslim Section, Kalmunai 32300, Sri Lanka	2026-01-26 17:05:09.25066	AMPARA	Kalmunai Muslim	7.41079270	81.83575620	t	\N	Nimal	EASTERN	400	2026-01-26 17:05:09.255157	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
8f3fc4d7-8ee1-46bb-9f3c-8eb0c65b22e3	Kalmunai Tamil Division, Sri Lanka	2026-01-26 17:05:51.796358	AMPARA	Kalmunai Tamil	7.42091270	81.82022300	t	\N	Nimal	EASTERN	400	2026-01-26 17:05:51.803812	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
3ad9756b-3704-4f73-a8d8-61ce380c21c6	Karaitivu, Sri Lanka	2026-01-26 17:08:29.454626	AMPARA	Karativu	7.37707770	81.84156160	t	\N	Nimal	EASTERN	400	2026-01-26 17:08:29.462245	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ccf799c9-6210-484c-b646-9af68b631112	Lahugala, Sri Lanka	2026-01-26 17:09:00.733295	AMPARA	Lahugala	6.87208720	81.72262980	t	\N	Nimal	EASTERN	400	2026-01-26 17:09:00.738575	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
52529e4f-a58c-4d1e-a31f-1668a83d20b3	Mahaoya, Sri Lanka	2026-01-26 17:09:41.845471	AMPARA	Mahaoya	7.53121600	81.35609520	t	\N	Nimal	EASTERN	400	2026-01-26 17:09:41.846478	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
4d34ac73-4a66-4e1e-9478-2eae3231a874	Navithanveli, Sri Lanka	2026-01-26 17:10:54.450834	AMPARA	Navithanveli	7.41256020	81.77690090	t	\N	Nimal	EASTERN	300	2026-01-26 17:10:54.451739	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
36be09b0-41c8-4eeb-92f9-4f8b2755a7af	Nintavur, Sri Lanka	2026-01-26 17:11:38.483723	AMPARA	Nindavur	7.33089250	81.84692120	t	\N	Nimal	EASTERN	300	2026-01-26 17:11:38.488755	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
34287f57-47ba-462c-b7a2-a31a19f064c7	Padiyatalawa, Sri Lanka	2026-01-26 17:12:10.703015	AMPARA	Padiyathalawa	7.39251260	81.24362270	t	\N	Nimal	EASTERN	400	2026-01-26 17:12:10.707567	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
6a3d6233-cd7c-4938-9814-09b54673f6a2	Sammanthurai, Sri Lanka	2026-01-26 17:13:23.286739	AMPARA	Sammanthurai	7.36320850	81.79752560	t	\N	Nimal	EASTERN	300	2026-01-26 17:13:23.290988	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2e37d740-6955-4b8f-b3e5-ebddfaa7da2c	Sainthamaruthu, Sri Lanka	2026-01-26 17:14:53.914805	AMPARA	Sandhamaradhu	7.39248050	81.83510450	t	\N	Nimal	EASTERN	300	2026-01-26 17:14:53.91681	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
77606368-aaaa-4d27-8245-3e50ea2b991a	Thirukkovil, Sri Lanka	2026-01-26 17:15:24.288984	AMPARA	Thirukkovil	7.11531220	81.85248820	t	\N	Nimal	EASTERN	300	2026-01-26 17:15:24.293984	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
408e8b14-cf9c-4f32-a9ef-b077eb733bc7	Uhana, Sri Lanka	2026-01-26 17:15:49.44682	AMPARA	Uhana	7.36311340	81.63588650	t	\N	Nimal	EASTERN	300	2026-01-26 17:15:49.452238	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
7af0668e-c5f6-4d03-be82-3fd102c31a87	Pottuvil, Sri Lanka	2026-01-26 17:12:43.849389	AMPARA	Pottuvil	6.87601820	81.82953910	t	\N	Nimal	EASTERN	400	2026-01-26 17:19:36.490947	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	7	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
03e86214-2262-42f8-af30-a95b93ec3953	Chenkaladi, Sri Lanka	2026-01-26 17:28:45.296048	BATTICALOA	Chenkalady	7.78585710	81.58980160	t	\N	Nimal	EASTERN	4000	2026-01-26 17:28:45.302376	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
5d111e98-c9b3-4c70-9bf7-4ee6eb1f07ac	Batticaloa, Sri Lanka	2026-01-26 17:27:53.971435	BATTICALOA	Batticaloa	7.72491460	81.69669110	t	\N	Nimal	EASTERN	5000	2026-01-26 17:29:13.13147	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	7	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
502fe166-d09d-4cd7-a112-f8a0d09ef0f9	Eravur, Sri Lanka	2026-01-26 17:30:29.276737	BATTICALOA	Eravur	7.77671390	81.60427040	t	\N	nimal	EASTERN	4000	2026-01-26 17:30:29.284254	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
8a8afcf1-6a6c-469e-b204-a49856a94265	Kaluwanchikudy, Sri Lanka	2026-01-26 17:30:58.893518	BATTICALOA	Kalawanchikudy	7.51747800	81.78713360	t	\N	Nimal	EASTERN	4000	2026-01-26 17:30:58.895506	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
b8e850a2-01b8-4043-8055-9e222b062c77	Karadiyanaru, Sri Lanka	2026-01-26 17:31:33.79833	BATTICALOA	Karadiyanaru	7.70074660	81.53838700	t	\N	Nimal	EASTERN	4000	2026-01-26 17:31:33.801718	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
fbd7b226-528a-4305-9a3b-cfdca7acbe77	Kattankudy, Sri Lanka	2026-01-26 17:32:05.611045	BATTICALOA	Kattankudy	7.68033170	81.72897850	t	\N	Nimal	EASTERN	4000	2026-01-26 17:32:05.61323	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
fb3382b0-8644-4526-b1bb-8be28bd7ef31	Kiran, Sri Lanka	2026-01-26 17:32:49.271107	BATTICALOA	Kiran	7.86777970	81.51026550	t	\N	Nimal	EASTERN	4000	2026-01-26 17:32:49.278748	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
0e68f9e3-04fa-477f-bb49-a4f3dbf7cdcd	Kokkadicholai, Sri Lanka	2026-01-26 17:33:39.541682	BATTICALOA	Kokkadicholai	7.61605700	81.71031550	t	\N	Nimal	EASTERN	3000	2026-01-26 17:33:39.547681	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
3b8a7600-d8eb-46c9-8ed6-c2b7bc184a49	Oddamavadi, Sri Lanka	2026-01-26 17:34:16.945075	BATTICALOA	Oddamavadi	7.90310760	81.52398460	t	\N	Nimal	EASTERN	3000	2026-01-26 17:34:16.951582	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
24fa12c6-a0f6-4ae9-b543-486bf49457d8	Rideethanna Main Rd, Sri Lanka	2026-01-26 17:34:51.728025	BATTICALOA	Rideethenna	7.96385520	81.34025950	t	\N	Nimal	EASTERN	4000	2026-01-26 17:34:51.736537	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
c700f3d6-95ce-41f8-9704-700e0d15327d	Thumpankerni (Y.f.s), Sri Lanka	2026-01-26 17:35:24.853953	BATTICALOA	Thumpankerni	7.54592140	81.69389330	t	\N	Nimal	EASTERN	4000	2026-01-26 17:35:24.858951	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
d47b3856-d6ab-4719-a823-c5158e2e55b5	Vakarai, Sri Lanka	2026-01-26 17:36:01.717083	BATTICALOA	Vakarai	8.13690990	81.43407900	t	\N	Nimal	EASTERN	4000	2026-01-26 17:36:01.721063	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a5890cc5-c2a5-401e-9cba-7187eaf352b8	Valaichchenai, Sri Lanka	2026-01-26 17:36:33.796759	BATTICALOA	Valachchenai	7.92132760	81.52467050	t	\N	Nimal	EASTERN	4000	2026-01-26 17:36:33.800272	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
1fc47c21-a462-477d-a5e4-ebcbb8c82868	Vavunathivu, Batticaloa, Sri Lanka	2026-01-26 17:37:07.185715	BATTICALOA	Vavunathivu	7.71663770	81.70010870	t	\N	Nimal	EASTERN	4000	2026-01-26 17:37:07.192177	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
74bc557d-5a0d-4256-ae50-a5f44d2f8642	Unnamed Road, 892F+XVQ, Verugal, Sri Lanka	2026-01-26 17:49:43.741496	TRINCOMALEE	Echchalampattu	8.30246240	81.37472060	t	\N	Nimal	EASTERN	400	2026-01-26 17:49:43.753062	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
8780115d-36fa-4ba4-a303-1cc4a8173bb3	Gomarankadawala, Sri Lanka	2026-01-26 18:06:32.455592	TRINCOMALEE	Gomarankadawala	8.67503840	80.96324230	t	\N	Nimal	EASTERN	300	2026-01-26 18:06:32.468435	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
9b0f94a1-e5f1-4e19-a4ca-f669feb572f4	Kantale, Sri Lanka	2026-01-26 18:07:08.592069	TRINCOMALEE	Kantale	8.36651780	81.00322030	t	\N	Nimal	EASTERN	400	2026-01-26 18:07:08.600849	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
0478fd9b-d3bd-475a-bf3f-eb4b30d7672a	Kinniya, Sri Lanka	2026-01-26 18:07:43.090042	TRINCOMALEE	Kinniya	8.50254730	81.18038320	t	\N	Nimal	EASTERN	400	2026-01-26 18:07:43.094016	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
d14c69d5-e42a-4814-a157-9a8c37888e87	Kuchchaveli, Sri Lanka	2026-01-26 18:08:15.287916	TRINCOMALEE	Kuchchuvely	8.82599550	81.09438420	t	\N	Nimal	EASTERN	400	2026-01-26 18:08:15.291483	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ed405a85-b4da-4ae4-a8c7-4a400f21c156	Morawewa, Sri Lanka	2026-01-26 18:08:47.497476	TRINCOMALEE	Morawewa	8.62793780	81.03346120	t	\N	Nimal	EASTERN	400	2026-01-26 18:08:47.505939	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
1677d184-da20-489d-9de5-23ecf0a50c2c	Mutur, Sri Lanka	2026-01-26 18:09:15.334755	TRINCOMALEE	Mutur	8.45790650	81.26840190	t	\N	Nimal	EASTERN	400	2026-01-26 18:09:15.338865	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
9aad0df3-5600-4f4f-ac19-de95496874cd	Padavi Sripura, Welioya, Sri Lanka	2026-01-26 18:09:43.537644	TRINCOMALEE	Padavisiripura	8.92496560	80.80871030	t	\N	Nimal	EASTERN	400	2026-01-26 18:09:43.542103	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
436116fc-c19e-4651-9acc-c1df63be7920	R/172, Thoppur road, Sumedhankarapura, Seruwawila, Sri Lanka	2026-01-26 18:10:27.613568	TRINCOMALEE	Seruwila	8.37631300	81.32094530	t	\N	Nimal	EASTERN	400	2026-01-26 18:10:27.620572	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
eb66f276-ea23-4729-b9ea-66c5167c0524	Thambalagamuwa, Sri Lanka	2026-01-26 18:10:58.752327	TRINCOMALEE	Thampalakamam	8.49737520	81.09202180	t	\N	Nimal	EASTERN	400	2026-01-26 18:10:58.757783	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
f03cc71c-5372-40cc-a300-e67d48195fab	Trincomalee, Sri Lanka	2026-01-26 18:11:34.763402	TRINCOMALEE	Trincomalee	8.58736380	81.21521210	t	\N	Nimal	EASTERN	500	2026-01-26 18:11:34.769685	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
80a79d80-cc0f-4c91-8faf-afb0ef358d31	8CJ8+QJ9, Harischandra Mawatha, Anuradhapura, Sri Lanka	2026-01-26 18:20:01.059545	ANURADHAPURA	Anuradhapura CNP	8.33191140	80.41659930	t	\N	Kamal	NORTH_CENTRAL	300	2026-01-26 18:20:01.07087	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
270e4609-cfa4-4d4d-8c82-8d1a5485a701	Anuradhapura, Sri Lanka	2026-01-26 18:20:33.38793	ANURADHAPURA	Anuradhapura ENP	8.31135180	80.40365080	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:20:33.389928	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
89c7ee70-02c4-44a7-8d12-49f5b2d74cce	Galenbindunuwewa, Sri Lanka	2026-01-26 18:21:00.219212	ANURADHAPURA	Galenbidunuwewa	8.29180470	80.71780770	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:21:00.227702	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
07e118f9-e502-490d-bc3c-98d5682f66c1	Galnewa, Sri Lanka	2026-01-26 18:21:48.275011	ANURADHAPURA	Galnewa	8.03583450	80.47990670	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:21:48.283532	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
0afe5987-189f-4f70-a209-83c087fa125f	Horowpothana, Sri Lanka	2026-01-26 18:22:17.964308	ANURADHAPURA	Horowpothana	8.55012150	80.83261050	t	\N	Kamal	NORTH_CENTRAL	2000	2026-01-26 18:22:17.966317	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
aff18dae-02e2-477e-90f6-ad7b1af5e8c8	Ipalogama, Sri Lanka	2026-01-26 18:22:54.370113	ANURADHAPURA	Ipologama	8.06502700	80.53551070	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:22:54.373572	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
eaa55aca-64ae-4159-a440-174368bf742e	Kahatagasdigiliya, Sri Lanka	2026-01-26 18:25:34.701423	ANURADHAPURA	Kahatagasdigiliya	8.42295420	80.69371770	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:25:34.70715	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2d6c32a2-2908-4fde-9dc5-1cadf85c8a69	Kebithigollewa, Sri Lanka	2026-01-26 18:26:00.491481	ANURADHAPURA	Kebithigollawa	8.64089160	80.66968160	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:26:00.493069	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
56dd391d-62cf-462a-9175-77d27880d12d	Kekirawa, Sri Lanka	2026-01-26 18:26:35.253991	ANURADHAPURA	Kekirawa	8.03984430	80.59839740	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:26:35.255741	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
3feb4402-e37f-4833-ba8d-ddde7a4627e7	Medawachchiya, Sri Lanka	2026-01-26 18:27:11.384937	ANURADHAPURA	Medawachchiya	8.53860050	80.49295200	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:27:11.384937	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
7b5f80ff-8150-4599-b13f-3ee49d3e44c2	Mihintale, Sri Lanka	2026-01-26 18:27:50.192997	ANURADHAPURA	Mihinthale	8.35342320	80.50494450	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:27:50.19732	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
9031e672-c4d3-4a62-a159-39f574907f6c	Nochchiyagama, Sri Lanka	2026-01-26 18:29:12.647055	ANURADHAPURA	Nochchiyagama	8.26643010	80.21649760	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:29:12.647055	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
75d40ff5-ac82-4fee-9633-97f9a665d80d	Palagala, Sri Lanka	2026-01-26 18:30:07.664665	ANURADHAPURA	Palagala	7.94746600	80.52022900	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:30:07.664665	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
5f2b4d04-108c-455b-a072-f38b5c7e8b15	Rajanganaya, Sri Lanka	2026-01-26 18:31:20.419512	ANURADHAPURA	Rajanganaya	8.16572070	80.19644430	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:31:20.42152	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
dffe29a4-7e7d-4dda-b6c6-794d38a8c790	Nachchaduwa, Sri Lanka	2026-01-26 18:28:24.932323	ANURADHAPURA	Nacchaduwa	8.25549640	80.48270740	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:28:24.932323	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
72a64b1c-f41b-43ff-be12-e0fc73abc822	Palugaswewa, Sri Lanka	2026-01-26 18:30:37.422733	ANURADHAPURA	Palugaswewa	8.06044070	80.68817180	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:30:37.424747	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
febab8fd-b06b-46ab-83f6-1ca5f908e1be	Rambewa, Sri Lanka	2026-01-26 18:31:55.22984	ANURADHAPURA	Rambawa	8.44010740	80.50494450	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:31:55.23397	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e97d8896-8352-472b-8c8e-8bb3733eb3e8	Padaviya, Sri Lanka	2026-01-26 18:29:38.786559	ANURADHAPURA	Padaviya	8.83409980	80.76073890	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:29:38.79125	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
094d805a-dc6d-4968-8d82-9edd1bc08095	Thalawa, Sri Lanka	2026-01-26 18:32:22.631217	ANURADHAPURA	Thalawa	8.14895130	80.40887340	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:32:22.637298	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
12625d8b-dbf4-4657-af54-aaaa8cea24a0	Tambuttegama, Sri Lanka	2026-01-26 18:33:04.551513	ANURADHAPURA	Thamutthegama	8.16125770	80.30017750	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:33:04.551513	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2eacdee2-9783-471e-bde6-51cda0f0b5c4	Thirappane, Sri Lanka	2026-01-26 18:33:59.025891	ANURADHAPURA	Thirappane	8.22200640	80.52261160	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:33:59.036467	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e76c787f-713b-4bfc-a21b-d2bef29716c8	Mahawilachchiya, Sri Lanka	2026-01-26 18:34:32.678298	ANURADHAPURA	Wilachchiya	8.47075510	80.20982190	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:34:32.680462	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
cc2b867c-4cb8-4a84-9fac-ea328882ef41	Aralaganwila, Sri Lanka	2026-01-26 18:42:31.942616	POLONNARUWA	Aralaganwila	7.76934940	81.17474980	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:42:31.951622	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
3ee5ee1f-5cce-4498-b5b0-6ff26d863bd1	Bakamuna, Sri Lanka	2026-01-26 18:43:03.994408	POLONNARUWA	Bakamoona	7.78060200	80.81840100	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:43:03.996406	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
9a595b6a-26fe-44c7-86b4-aaa0e38be853	Lankapura, Sri Lanka	2026-01-26 18:43:44.26594	POLONNARUWA	Lankapura	8.04194510	81.07811360	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:43:44.267936	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
7377a7ac-b7ea-4cf3-8914-68abebcfad88	Medirigiriya, Sri Lanka	2026-01-26 18:44:13.771625	POLONNARUWA	Medirigiriya	8.15053300	80.97898200	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:44:13.777694	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
67ebc4ff-e780-4aae-aa4d-b8a3ca928456	Minneriya, Sri Lanka	2026-01-26 18:44:44.921439	POLONNARUWA	Minneriya	8.03867090	80.90639220	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:44:44.925847	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
c6a07e12-e5c7-4382-9f6a-ba3a2a7ce71a	Polonnaruwa, Sri Lanka	2026-01-26 18:45:19.621442	POLONNARUWA	Polonnaruwa	7.91470300	81.00011830	t	\N	Kamal	NORTH_CENTRAL	400	2026-01-26 18:45:19.621442	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
bc1bf4ef-2973-4110-9362-4d4e1033ae7d	Welikanda, Sri Lanka	2026-01-26 18:45:51.955095	POLONNARUWA	Welikanda	7.94641600	81.24776480	t	\N	Kamal	NORTH_CENTRAL	800	2026-01-26 18:45:51.957093	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
f0e1a6e3-9881-45e9-9fc9-8fef3053df77	Chavakachcheri, Sri Lanka	2026-01-26 19:21:36.311797	JAFFNA	Chavakachcheri	9.65962330	80.16073380	t	\N	Kamal	NORTHERN	4000	2026-01-26 19:21:36.33596	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
583ec334-45d8-408d-8741-0b8c6daab4aa	Jaffna, Sri Lanka	2026-01-26 19:22:17.873372	JAFFNA	Jaffna	9.66059900	80.01170230	t	\N	Kamal	NORTHERN	500	2026-01-26 19:22:17.883866	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
f7cc17b9-2dd2-43ec-889a-fcc60eb07f4c	Hospital Rd, Jaffna, Sri Lanka	2026-01-26 19:23:16.495324	JAFFNA	Jaffna Vet. Hospital	9.66683310	80.01196860	t	\N	Kamal	NORTHERN	4000	2026-01-26 19:23:16.497792	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
81fa5008-0d1f-47bc-b607-f0285228fcf0	Karaveddi, Sri Lanka	2026-01-26 19:23:46.404555	JAFFNA	Karaveddy	9.80006730	80.19941820	t	\N	Kamal	NORTHERN	500	2026-01-26 19:23:46.413547	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
429bf0d3-1b8a-4e40-bae6-b527795f9021	Karainagar, Sri Lanka	2026-01-26 19:24:13.962318	JAFFNA	Karainagar	9.74291210	79.88169620	t	\N	Kamal	NORTHERN	600	2026-01-26 19:24:13.964836	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
eb799bac-acc4-49b4-a736-744f1b46c81f	Kayts, Sri Lanka	2026-01-26 19:24:45.523683	JAFFNA	Kayts	9.69789370	79.86048310	t	\N	Kamal	NORTHERN	400	2026-01-26 19:24:45.523683	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
f19fe104-e638-43a7-a58e-f2346e6cbf41	JC94+V3X, Sri Lanka	2026-01-26 19:26:15.801343	JAFFNA	Maruthankerny	9.61973730	80.40521900	t	\N	Kamal	NORTHERN	700	2026-01-26 19:26:15.80234	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
8a30c051-299b-43b4-a599-0382a8aeca95	Nallur, Jaffna, Sri Lanka	2026-01-26 19:26:49.12944	JAFFNA	Nallur	9.67015470	80.03952050	t	\N	Kamal	NORTHERN	3000	2026-01-26 19:26:49.133707	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
66cc87a6-e7fa-4195-b1a2-e1ca55e904e8	Point Pedro, Sri Lanka	2026-01-26 19:27:16.310894	JAFFNA	Point Pedro	9.79378850	80.22097730	t	\N	Kamal	NORTHERN	2000	2026-01-26 19:27:16.313653	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
31b3b356-4f6f-4deb-9369-8c08e3325ba9	Sandilipay, Sri Lanka	2026-01-26 19:27:43.530108	JAFFNA	Sandilipai	9.74180700	79.98627360	t	\N	Kamal	NORTHERN	500	2026-01-26 19:27:43.533119	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
5f9f46aa-ce8b-4997-9dfb-9607abef2e7b	Tellippalai, Sri Lanka	2026-01-26 19:28:39.665225	JAFFNA	Tellippalai	9.78562120	80.03364820	t	\N	Kamal	NORTHERN	400	2026-01-26 19:28:39.665225	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
9be18bdc-263b-4ffd-b3fe-4ce0f4b73150	Uduvil, Sri Lanka	2026-01-26 19:29:09.558729	JAFFNA	Uduvil	9.73439920	80.01094020	t	\N	Kamal	NORTHERN	200	2026-01-26 19:29:09.569851	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
b17cc979-4219-40a8-a253-e088b47fbfe6	Vaddukoddai, Sri Lanka	2026-01-26 19:29:40.393802	JAFFNA	Vaddukkodai	9.74183130	79.95087820	t	\N	Kamal	NORTHERN	300	2026-01-26 19:29:40.395809	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ec785ed1-4c85-4e5d-a4e8-839c09b46ae9	Velanai, Sri Lanka	2026-01-26 19:30:07.084952	JAFFNA	Velanai	9.63724050	79.90107840	t	\N	Kamal	NORTHERN	300	2026-01-26 19:30:07.087293	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
95fb8319-8c3c-4f64-8a0f-b3128490396e	Delft, Sri Lanka	2026-01-26 19:30:39.871408	JAFFNA	Delft	9.51732650	79.70042360	t	\N	Kamal	NORTHERN	400	2026-01-26 19:30:39.874683	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
595d8e26-4956-45e7-9ea5-dc135bd7d674	CFHJ+HV7, Velikkandal, Sri Lanka	2026-01-26 19:41:13.461069	KILINOCHCHI	Kandawalai	9.42891760	80.48218090	t	\N	Sunil	NORTHERN	400	2026-01-26 19:41:13.461069	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
21cd575c-2d39-47a3-84be-9cf764a5127c	Kopay Centre, Sri Lanka	2026-01-26 19:25:17.863907	JAFFNA	Kopay	9.70915560	80.05628640	t	\N	Kamal	NORTHERN	3000	2026-01-26 19:38:59.802323	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	9	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
de1d801a-9e72-402c-b6c7-6433eb943e16	Kilinochchi, Sri Lanka	2026-01-26 19:41:41.945716	KILINOCHCHI	Kilinochchi	9.37584560	80.37175730	t	\N	Sunil	NORTHERN	300	2026-01-26 19:41:41.947725	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
4518a71e-9aae-4d59-bebf-8c874313faf3	Pachchilaipalli, Pachchilapalli, Sri Lanka	2026-01-26 19:42:11.384033	KILINOCHCHI	Pachchilaipalli	9.60873090	80.33093590	t	\N	Sunil	NORTHERN	300	2026-01-26 19:42:11.386042	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
29255e56-38a1-4cbb-9684-af70e2d58dfc	Poonakary, Sri Lanka	2026-01-26 19:42:45.215996	KILINOCHCHI	Poonakary	9.50408400	80.21223100	t	\N	Sunil	NORTHERN	3000	2026-01-26 19:42:45.220584	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e7c00d2e-4f10-44f0-9c02-f3ff3308f00a	Madu, Sri Lanka	2026-01-26 19:47:47.269795	MANNAR	Madhu	8.85636100	80.20424360	t	\N	Sunil	NORTHERN	200	2026-01-26 19:47:47.277456	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
258d33f5-f348-4479-9e8d-feacdeb65f80	Mannar, Sri Lanka	2026-01-26 19:48:21.17385	MANNAR	Mannar	8.98097430	79.90441490	t	\N	Sunil	NORTHERN	1000	2026-01-26 19:48:21.181753	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
937ff8ec-4520-4593-9bd2-0bda3902c29e	Manthai West, Sri Lanka	2026-01-26 19:48:50.541258	MANNAR	Manthai west	9.01015160	80.06376030	t	\N	Sunil	NORTHERN	200	2026-01-26 19:48:50.545332	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ac26e74d-0eae-4b25-9c8b-c77a8e7c6929	Murunkan, Sri Lanka	2026-01-26 19:49:21.714152	MANNAR	Murunkan	8.83279700	80.03393120	t	\N	Sunil	NORTHERN	300	2026-01-26 19:49:21.722448	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
f5fd144d-192d-4c13-97d6-9f7b5215ae02	Musali, Sri Lanka	2026-01-26 19:49:46.324038	MANNAR	Musali	8.72903660	79.96247840	t	\N	Sunil	NORTHERN	200	2026-01-26 19:49:46.326562	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
526211c9-cb82-47a4-8fa0-fd15c126f03f	Cheddikulam, Sri Lanka	2026-01-26 21:06:53.73602	VAVUNIYA	Cheddikulam	8.66559560	80.29623850	t	\N	Sunil	NORTHERN	300	2026-01-26 21:06:53.741046	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
eed37728-8aaf-4768-9862-570f31137f21	Kanakarayankulam, Sri Lanka	2026-01-26 21:07:26.264574	VAVUNIYA	Kanagarayankulam (Vavuniya North)	9.05000050	80.51606080	t	\N	Sunil	NORTHERN	200	2026-01-26 21:07:26.266343	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
808bc30c-2f12-4779-8817-cc1313a3fd0c	Vavuniya, Sri Lanka	2026-01-26 21:08:00.284495	VAVUNIYA	Vavuniya	8.75420290	80.49824020	t	\N	Sunil	NORTHERN	10000	2026-01-26 21:08:00.284495	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
1aff661a-2868-4608-a0de-c9a0930b2bc1	Town, QF6V+WFH, Vavuniya, Sri Lanka	2026-01-26 21:09:00.655855	VAVUNIYA	Vavuniya South	8.76232400	80.49372970	t	\N	Sunil	NORTHERN	200	2026-01-26 21:09:00.661867	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
81ac85a3-30bf-435b-ad7b-68c115be52b9	Manthai East, Sri Lanka	2026-01-26 21:11:50.615759	MULLAITIVU	Manthai East	9.05607280	80.29349670	t	\N	Sunil	NORTHERN	1000	2026-01-26 21:11:50.629398	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
f3b340b3-1eb8-4ed0-9e2d-6ac315d27f25	7RC7+5V8, Mullaitivu, Sri Lanka	2026-01-26 21:12:27.166783	MULLAITIVU	Mulaithivu (Marimeipattu)	9.27040970	80.81468860	t	\N	Sunil	NORTHERN	200	2026-01-26 21:12:27.170985	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ef5bea47-5253-4c8c-a247-6155850e87a2	Puthukkudiyiruppu, Sri Lanka	2026-01-26 21:13:50.303897	MULLAITIVU	Puthukkudiyiruppu	9.31380980	80.69636890	t	\N	Sunil	NORTHERN	200	2026-01-26 21:13:50.305911	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
bba20175-5ca7-4979-8c61-cc1596ca6401	Oddusuddan, Sri Lanka	2026-01-26 21:13:04.602779	MULLAITIVU	Oddusuddan	9.15386780	80.64973120	t	\N	Sunil	NORTHERN	200	2026-01-26 21:13:14.16778	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	5	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2654becc-055b-4443-942b-3d83a7d8d232	Thunukkai, Sri Lanka	2026-01-26 21:14:14.714605	MULLAITIVU	Thunukkai	9.13404860	80.28626360	t	\N	Sunil	NORTHERN	200	2026-01-26 21:14:14.719781	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
31d30778-3b8e-4d74-908c-975355497c09	Welioya, Sri Lanka	2026-01-26 21:14:42.025916	MULLAITIVU	Weli Oya (Vali South)	8.96404140	80.78794070	t	\N	Sunil	NORTHERN	200	2026-01-26 21:14:42.034219	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
cc49b0bb-5fc1-4141-9ba1-61f3fef971ce	Alawwa, Sri Lanka	2026-01-26 21:18:18.080365	KURUNEGALA	Alawwa	7.29543020	80.23662610	t	\N	Nimal	NORTH_WESTERN	200	2026-01-26 21:18:18.081441	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
14478c31-6429-46b5-a621-8f32ef65f14e	Ambanpola, Sri Lanka	2026-01-26 21:18:49.182021	KURUNEGALA	Ambanpola	7.91811440	80.24049590	t	\N	Nimal	NORTH_WESTERN	200	2026-01-26 21:18:49.184033	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
0a46b083-5b65-49dc-8c40-092b1c51c56e	Bamunakotuwa, Sri Lanka	2026-01-26 21:19:28.00008	KURUNEGALA	Bamunakotuwa	7.57168450	80.24891850	t	\N	Nimal	NORTH_WESTERN	300	2026-01-26 21:19:28.00008	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
05b8eec4-8f03-458f-b3af-0271d786e08d	Bingiriya, Sri Lanka	2026-01-26 21:19:58.21651	KURUNEGALA	Bingiriya	7.59822340	79.93721900	t	\N	Nimal	NORTH_WESTERN	200	2026-01-26 21:19:58.220386	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
5b687388-fa3a-4390-8999-473214060280	Dummalasuriya, Sri Lanka	2026-01-26 21:20:27.544543	KURUNEGALA	Dummalasooriya	7.49240010	79.91087440	t	\N	Nimal	NORTH_WESTERN	200	2026-01-26 21:20:27.55017	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
6d779d64-21aa-4245-afc5-bac349a64614	Ehetuwewa, Sri Lanka	2026-01-26 21:20:58.450186	KURUNEGALA	Ehatuwewa	7.93567610	80.34618760	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:20:58.450186	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
32008ee1-8a15-4ef1-9016-f011c44d100a	Galgamuwa, Sri Lanka	2026-01-26 21:21:25.534435	KURUNEGALA	Galgamuwa	7.98649440	80.28787940	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:21:25.536444	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ee87a62e-ceed-43ca-ad08-5a6181382604	Ganewatta, Sri Lanka	2026-01-26 21:21:52.660212	KURUNEGALA	Ganewatta	7.65570370	80.34925920	t	\N	Nimal	NORTH_WESTERN	200	2026-01-26 21:21:52.67236	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
de5947c1-0dc2-400a-837a-e0f69216b883	Giribawa, Sri Lanka	2026-01-26 21:22:30.441077	KURUNEGALA	Giribawa	8.11600820	80.19583370	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:22:30.441077	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
464d2563-aee0-48fb-80c5-35cd57c507a0	Horombawa, Sri Lanka	2026-01-26 21:23:04.524599	KURUNEGALA	Horombawa	7.45722470	80.16797990	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:23:04.530367	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
7f3e82cf-dc79-46e7-b7c9-3fa93e8c2e4a	Ibbagamuwa, Sri Lanka	2026-01-26 21:23:30.576233	KURUNEGALA	Ibbagamuwa	7.54779240	80.44976560	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:23:30.580271	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
0bd7b00e-f163-404b-b32c-ac99b965c941	Katupotha, Sri Lanka	2026-01-26 21:24:00.412379	KURUNEGALA	Katupotha	7.55053450	80.19086420	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:24:00.412379	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
1024b8d1-2902-4a32-a54e-6753db32267f	Kobeigane, Sri Lanka	2026-01-26 21:24:29.070797	KURUNEGALA	Kobeigane	7.65557320	80.12622870	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:24:29.070797	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
49d42310-90ea-4dd1-8765-f2f7ef89416d	Kotawehera, Sri Lanka	2026-01-26 21:24:59.710407	KURUNEGALA	Kotawehera	6.59361110	80.91638890	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:24:59.714559	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
dc877c5b-724c-4c68-a9e6-7f9169fff0a9	Kurunegala, Sri Lanka	2026-01-26 21:26:11.896168	KURUNEGALA	Kurunegala	7.48176950	80.36088760	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:26:11.898433	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
74f03f06-d787-4d6b-833c-ae0f9f9be537	Maho, Sri Lanka	2026-01-26 21:26:38.917349	KURUNEGALA	Maho	7.82389840	80.27345030	t	\N	Nimal	NORTH_WESTERN	2000	2026-01-26 21:26:49.274495	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	7	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e8dc25bf-6d01-4038-9cd5-60c61f5b47b6	Kuliyapitiya, Sri Lanka	2026-01-26 21:25:31.292112	KURUNEGALA	Kuliyapitiya	7.47017690	80.04398230	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:25:31.294121	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
b29d550f-af7d-4ed5-afac-e9c4445020f9	Mawathagama, Sri Lanka	2026-01-26 21:27:23.684098	KURUNEGALA	Mawathagama	7.43206300	80.44797090	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:27:23.684098	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
17933e60-ab6f-4583-912c-78f87201a24b	Narammala, Sri Lanka	2026-01-26 21:27:49.541919	KURUNEGALA	Narammala	7.43069150	80.21427310	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:27:49.545422	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
569ad50b-9523-4271-8e3f-696b1fcb9df5	Nikaweratiya, Sri Lanka	2026-01-26 21:28:19.862807	KURUNEGALA	Nikaweratiya	7.74638610	80.13169270	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:28:19.866909	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
115fb6c6-8694-44c9-819f-68a66fbbfb61	Panduwasnuwara, Sri Lanka	2026-01-26 21:28:52.024382	KURUNEGALA	Panduwasnuwara	7.60382010	80.10656460	t	\N	Nimal	NORTH_WESTERN	30000	2026-01-26 21:28:52.029405	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2656606a-bca0-4fff-a250-d5f3fb8cbaae	Pannala, Sri Lanka	2026-01-26 21:29:22.314187	KURUNEGALA	Pannala	7.32820490	80.02415850	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:29:22.324558	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
afec7aa1-fbf6-40bd-a7ed-a85c0896283f	Polgahawela, Sri Lanka	2026-01-26 21:29:48.759029	KURUNEGALA	Polgahawela	7.33530400	80.30016540	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:29:48.759029	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
8ee885ce-cd59-4839-bff0-4cf8989e56e7	Polpithigama, Sri Lanka	2026-01-26 21:30:15.074218	KURUNEGALA	Polpithigama	7.82151680	80.40531390	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:30:15.074218	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2ba4af9c-bbe9-4453-90ac-8221e0639195	Rasnayakapura, Sri Lanka	2026-01-26 21:30:51.272795	KURUNEGALA	Rasnayakapura	7.77654060	80.01463140	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:30:51.276484	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
b9facc28-0b0b-4056-a3ce-1092464669f4	Ridigama, Sri Lanka	2026-01-26 21:31:18.748644	KURUNEGALA	Ridigama	7.54952460	80.49105910	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:31:18.748644	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
291bfe25-a3d9-4c66-9b13-25a3db7dbcd5	Wariyapola, Sri Lanka	2026-01-26 21:31:45.274396	KURUNEGALA	Wariyapola	7.61983360	80.24599000	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:31:45.281269	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
978252da-3e15-4a32-92b4-b25f55a8f8bd	Weerambugedara, Sri Lanka	2026-01-26 21:32:10.988233	KURUNEGALA	Weerambugedara	7.47632710	80.24230640	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:32:10.994229	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
1040de28-02b2-48df-8ae1-37a70530669f	Mallawapitiya, Sri Lanka	2026-01-26 21:32:37.854366	KURUNEGALA	Mallawapitiya	7.47480500	80.38865610	t	\N	Nimal	NORTH_WESTERN	200	2026-01-26 21:32:37.854366	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
88a72d8f-7d07-4c7d-b8b1-08ccdb7ffe99	Maspotha, Sri Lanka	2026-01-26 21:33:02.804449	KURUNEGALA	Maspotha	7.54063080	80.31328290	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:33:02.807271	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
0e21faf4-9082-4d8a-8c6d-b4c9b67729d2	Anamaduwa, Sri Lanka	2026-01-26 21:39:51.999297	PUTTALAM	Anamaduwa	7.87777030	80.01111560	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:39:52.009528	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
44b63d4b-2ee9-498a-9dc4-3400df14e9f9	Arachchikattuwa, Sri Lanka	2026-01-26 21:40:48.33469	PUTTALAM	Arachchikattuwa	7.66670940	79.83593540	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:40:48.342791	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a08046d3-b77e-4408-9562-161c586b8559	Chilaw, Sri Lanka	2026-01-26 21:41:56.465894	PUTTALAM	Chilaw	7.57771550	79.79438650	t	\N	Nimal	NORTH_WESTERN	300	2026-01-26 21:41:56.473417	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
89006adb-0a53-4aa6-a956-7e8b6b804281	Dankotuwa, Sri Lanka	2026-01-26 21:42:27.266902	PUTTALAM	Dankotuwa	7.29746350	79.88218810	t	\N	Nimal	NORTH_WESTERN	400	2026-01-26 21:42:27.268919	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
566f402d-ef2d-44e6-b319-dc32a40f7703	Kalpitiya, Sri Lanka	2026-01-26 21:42:57.646262	PUTTALAM	Kalpitiya	8.22952810	79.75961420	t	\N	Nimal	NORTH_WESTERN	400	2026-01-26 21:42:57.652795	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
996e5154-2247-45cc-9113-4f9dd5f378a5	Karuwalagaswewa, Sri Lanka	2026-01-26 21:43:37.748562	PUTTALAM	Karuwalagaswewa	8.05445310	79.95207910	t	\N	Nimal	NORTH_WESTERN	500	2026-01-26 21:43:37.752624	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
7601d450-5bf4-42d8-810f-35bcffe44f46	Madampe, Sri Lanka	2026-01-26 21:44:06.923479	PUTTALAM	Madampe	7.49590920	79.84228350	t	\N	Nimal	NORTH_WESTERN	200	2026-01-26 21:44:06.929801	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
3d5236aa-a4da-413a-8eee-e20c270265e2	Mahakumbukkadawala, Sri Lanka	2026-01-26 21:44:39.607821	PUTTALAM	Mahakumbukkadawala	7.84851760	79.90360570	t	\N	Nimal	NORTH_WESTERN	200	2026-01-26 21:44:39.610069	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2ce70e93-cf96-4219-9408-b17b40e78252	Mahawewa, Sri Lanka	2026-01-26 21:45:23.614858	PUTTALAM	Mahawewa	7.45946220	79.82627720	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:45:23.619037	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
f13f93b8-1c8a-4fb4-9e5c-fd4691fcd732	Marawila, Sri Lanka	2026-01-26 21:46:09.027494	PUTTALAM	Marawila	7.41306300	79.83090740	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:46:09.030513	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
5c143f73-49af-4b0e-8fc3-e41e4e55ed66	Mundalama, Sri Lanka	2026-01-26 21:46:40.848083	PUTTALAM	Mundalama	7.81982770	79.83527670	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:46:40.850089	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
64b5908d-64dd-4ec3-824d-08d5650772a8	Nawagattegama, Sri Lanka	2026-01-26 21:47:17.479473	PUTTALAM	Nawagaththegama	8.00653970	80.11214930	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:47:17.480473	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
fec1a952-c810-4b0f-aa73-4c0199b6931a	Puttalam, Sri Lanka	2026-01-26 21:47:48.273913	PUTTALAM	Puttalam	8.03026860	79.83148000	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:47:48.277333	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
c6d43015-50ea-42d3-84f8-c261704eeda0	Serukele, Sri Lanka	2026-01-26 21:48:13.427727	PUTTALAM	Serukele	7.72661490	79.91813320	t	\N	Nimal	NORTH_WESTERN	100	2026-01-26 21:48:13.430312	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
124404e8-ad21-4845-b9b9-7e38a21fad61	Wanathavilluwa, Sri Lanka	2026-01-26 21:48:41.628713	PUTTALAM	Wanathawilluwa	8.18693150	79.86118320	t	\N	Nimal	NORTH_WESTERN	300	2026-01-26 21:48:41.63171	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
9e07b8d7-f137-464f-8f89-d506894f2ca1	8RRR+6XX, Wennappuwa, Sri Lanka	2026-01-26 21:49:26.176742	PUTTALAM	Vennappuwa	7.34063490	79.84222430	t	\N	Nimal	NORTH_WESTERN	500	2026-01-26 21:49:26.178288	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
11f44d20-7ed1-4f52-b077-008ad4e75beb	Aranayaka, Sri Lanka	2026-01-26 21:54:47.948593	KEGALLE	Aranayaka	7.14879150	80.46485880	t	\N	Kamal	SABARAGAMUWA	100	2026-01-26 21:54:47.954793	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ed147a3b-dc32-4a9b-9f90-f8be23876028	Bulathkohupitiya, Sri Lanka	2026-01-26 21:55:21.628764	KEGALLE	Bulathkohupitiya	7.10501560	80.33584050	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 21:55:21.631225	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2fc9dad2-b4a0-4e5a-8bb9-36f560de3459	Deraniyagala, Sri Lanka	2026-01-26 21:55:47.635138	KEGALLE	Deraniyagala	6.92725590	80.33852140	t	\N	Kamal	SABARAGAMUWA	300	2026-01-26 21:55:47.640684	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a7b2bff8-f3af-44c1-b94f-4f7fee43df02	Dehiovita, Sri Lanka	2026-01-26 22:11:23.611348	KEGALLE	Dehiovita	6.96672080	80.26590540	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:11:23.625323	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ca1fb1ce-9a1c-4ed4-ae5a-349a5d623f86	Galigamuwa, Sri Lanka	2026-01-26 22:12:11.379347	KEGALLE	Galigamuwa	7.23528130	80.31017670	t	\N	Kamal	SABARAGAMUWA	100	2026-01-26 22:12:11.381361	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
726cbe0a-861d-4709-b536-fdbc084c2f71	Kegalle, Sri Lanka	2026-01-26 22:12:44.448661	KEGALLE	Kegalle	7.25272830	80.34177910	t	\N	Kamal	SABARAGAMUWA	2000	2026-01-26 22:12:44.456484	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a5b922b0-ac04-4ef3-ba67-1090ee5ce2b4	Mawanella, Sri Lanka	2026-01-26 22:13:29.967164	KEGALLE	Mawanella	7.25219540	80.44682750	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:13:29.967164	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
98f384bf-dc94-411c-b7ca-0aefd0e5186c	Rambukkana, Sri Lanka	2026-01-26 22:14:08.513093	KEGALLE	Rambukkana	7.32190950	80.39170360	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:14:08.515608	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
cb1de2b8-8497-42bd-90f4-b0610daf085f	Ruwanwella, Sri Lanka	2026-01-26 22:14:45.849094	KEGALLE	Ruwanwella	7.04577110	80.25380590	t	\N	Kamal	SABARAGAMUWA	300	2026-01-26 22:14:45.853595	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
b4bef680-c679-47a0-ab0e-0e7104b90947	Undugoda, Sri Lanka	2026-01-26 22:15:28.295663	KEGALLE	Undugoda	7.13977700	80.35751280	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:15:28.299932	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ce435824-26e2-45d1-85a2-3357a6ca6993	Warakapola, Sri Lanka	2026-01-26 22:16:03.922032	KEGALLE	Warakapola	7.22652000	80.19912810	t	\N	Kamal	SABARAGAMUWA	500	2026-01-26 22:16:03.925473	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e516899b-e8bc-4cec-bf1e-650fa2f94dfd	Yatiyanthota, Sri Lanka	2026-01-26 22:16:32.925606	KEGALLE	Yatiyanthota	7.02888240	80.29554190	t	\N	Kamal	SABARAGAMUWA	100	2026-01-26 22:16:32.925606	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
741c2838-437a-4715-a451-24975abffaf2	Ayagama, Sri Lanka	2026-01-26 22:22:00.441829	RATNAPURA	Ayagama	6.63872000	80.31204000	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:22:00.451899	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
43d34dc2-0894-4eb8-9ab3-bf49c5a212de	Balangoda, Sri Lanka	2026-01-26 22:22:33.044131	RATNAPURA	Balangoda	6.64567210	80.69215500	t	\N	Kamal	SABARAGAMUWA	300	2026-01-26 22:22:33.045497	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e5c5b417-b034-4ddf-923a-4fae1d30b248	Eheliyagoda, Sri Lanka	2026-01-26 22:23:34.419613	RATNAPURA	Eheliyagoda	6.85087670	80.26415850	t	\N	Kamal	SABARAGAMUWA	500	2026-01-26 22:23:34.428093	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
f3a91aa9-4bfa-4527-8d02-6cf3399dc9ef	Elapatha, Sri Lanka	2026-01-26 22:24:43.239882	RATNAPURA	Elapatha	6.65574300	80.36779080	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:24:43.241893	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
83cd4d4b-c1d3-405a-b8c9-582f57b6d451	Embilipitiya, Sri Lanka	2026-01-26 22:25:18.258202	RATNAPURA	Embilipitiya	6.33278870	80.85533110	t	\N	Kamal	SABARAGAMUWA	3000	2026-01-26 22:25:18.260998	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e51c1ae5-d16f-4d18-b1e8-9dff64a84438	Godakawela, Sri Lanka	2026-01-26 22:25:59.845568	RATNAPURA	Godakawela	6.50438640	80.65150830	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:25:59.848696	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
493d9911-17e5-441d-88c2-a46e63d43208	Kahawatta, Sri Lanka	2026-01-26 22:27:04.384816	RATNAPURA	Kahawatta	6.57865290	80.57433000	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:27:04.391275	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
46f5cb53-11d3-4dc5-a22b-dd73eebb9ffd	Kalawana, Sri Lanka	2026-01-26 22:27:40.32968	RATNAPURA	Kalawana	6.53112410	80.39649460	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:27:40.332684	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
3c139070-82c3-483c-aca3-28a6fded2ddc	Kalthota, Sri Lanka	2026-01-26 22:28:13.44772	RATNAPURA	Kaltota	6.65939600	80.87098720	t	\N	Kamal	SABARAGAMUWA	500	2026-01-26 22:28:13.449718	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
d7019a29-8d24-40fa-8a3a-438dbe521bd8	Kuruwita, Sri Lanka	2026-01-26 22:30:00.332023	RATNAPURA	Kuruwita	6.77701020	80.36634260	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:30:00.334005	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
21a7b528-9327-4700-a142-551053836ce0	Nivithigala, Sri Lanka	2026-01-26 22:30:48.663428	RATNAPURA	Niwithigala	6.59588500	80.45776700	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:30:48.665436	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
7db18ba8-f269-42bc-b6bb-dc07c3fc5112	Welange, Sri Lanka	2026-01-26 22:32:40.910699	RATNAPURA	Opanayaka	6.62375960	80.68743720	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:32:40.913708	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
f4e99e9b-6c98-4cc0-8325-356616abda3a	Pelmadulla, Sri Lanka	2026-01-26 22:33:18.348624	RATNAPURA	Pelmadulla	6.62347450	80.54313300	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:33:18.351639	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
19a2626f-85bf-4555-bf7c-4027cf1d1dc2	Rakwana, Sri Lanka	2026-01-26 22:33:55.984697	RATNAPURA	Rakwana	6.46472370	80.61604020	t	\N	Kamal	SABARAGAMUWA	400	2026-01-26 22:33:55.986684	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2fd3e25b-9dc1-41d2-b8f7-32a105fcaeee	Rathnapura Town, Ratnapura, Sri Lanka	2026-01-26 22:34:33.725128	RATNAPURA	Rathnapura	6.67630540	80.40552630	t	\N	Kamal	SABARAGAMUWA	300	2026-01-26 22:34:33.734756	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ead95db6-2ea2-4c5f-aa6d-bfadc2a7eb43	Suriyakanda, Sri Lanka	2026-01-26 22:35:02.403912	RATNAPURA	Sooriyakanda	6.43353960	80.63824130	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:35:02.414079	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
75f3a436-82d2-4933-a44b-7d5623f1875a	Meegas Ara, Sri Lanka	2026-01-26 22:35:35.482624	RATNAPURA	Meegas Ara	6.20200950	80.76855080	t	\N	Kamal	SABARAGAMUWA	500	2026-01-26 22:35:35.484626	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
dbc6e90d-ab71-4eca-bc6d-313e42359bf6	Weligepola, Sri Lanka	2026-01-26 22:36:05.904498	RATNAPURA	Weligepola	6.57180590	80.70480840	t	\N	Kamal	SABARAGAMUWA	200	2026-01-26 22:36:05.906496	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
cfcf37ef-1902-485a-8e27-dd297660d37b	Ambalangoda, Sri Lanka	2026-01-26 22:43:59.765719	GALLE	Ambalangoda	6.24415210	80.05908040	t	\N	Sunil	SOUTHERN	400	2026-01-26 22:43:59.77291	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
83174eef-4ccb-43be-8b03-acd8809503e7	Kiriella, Sri Lanka	2026-01-26 22:29:24.673921	RATNAPURA	Kiriella	6.74581800	80.27000360	t	\N	Kamal	SABARAGAMUWA	400	2026-01-26 22:41:44.425894	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	9	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
dd620cda-5dc4-4891-bf26-912fdf2018e3	Akmeemana, Sri Lanka	2026-01-26 22:45:27.602142	GALLE	Akmeemana	6.08363060	80.29623850	t	\N	Sunil	SOUTHERN	300	2026-01-26 22:45:27.605137	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
91fd0b37-8b45-4913-9125-34dc80830ffc	Baddegama, Sri Lanka	2026-01-26 22:46:20.169519	GALLE	Baddegama	6.16882920	80.17939760	t	\N	Sunil	SOUTHERN	300	2026-01-26 22:46:20.174534	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
0981584c-b590-4211-913a-2bb73fb5a300	Balapitiya, Sri Lanka	2026-01-26 22:47:05.629728	GALLE	Balapitiya	6.27835460	80.04030470	t	\N	Sunil	SOUTHERN	400	2026-01-26 22:47:05.632729	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
85843ca5-3709-4f96-b4c3-c709b737cfa1	Bentota, Sri Lanka	2026-01-26 22:47:37.487832	GALLE	Benthota	6.41876040	80.00245500	t	\N	Sunil	SOUTHERN	200	2026-01-26 22:47:37.495351	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
9315b17b-ea85-46f0-8c8b-cbb65a17fa9b	Bope-Poddala, Sri Lanka	2026-01-26 22:48:18.807853	GALLE	Bopepoddala  (Galle)	6.08791090	80.21180270	t	\N	Sunil	SOUTHERN	500	2026-01-26 22:48:18.809868	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
b03a02da-4588-4bf4-85dc-1086583fabdd	Elpitiya, Sri Lanka	2026-01-26 22:48:58.86623	GALLE	Elpitiya	6.29038990	80.16248570	t	\N	Sunil	SOUTHERN	400	2026-01-26 22:48:58.86917	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
c007a0e1-a14c-4cfb-bdca-616c3fcf8bbd	Gonapinuwala, Sri Lanka	2026-01-26 22:49:38.224918	GALLE	Gonapeenuwala	6.14139610	80.13690750	t	\N	Sunil	SOUTHERN	3000	2026-01-26 22:49:38.228041	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
28520c25-50a5-424a-94a2-21afe5456b8d	Habaraduwa, Sri Lanka	2026-01-26 22:50:20.52258	GALLE	Habaraduwa	5.99818910	80.30901520	t	\N	Sunil	SOUTHERN	200	2026-01-26 22:50:20.524583	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e91999d9-ab09-4217-b703-07e3e4b6e490	Hiniduma, Sri Lanka	2026-01-26 22:51:05.355384	GALLE	Hiniduma	6.30944820	80.32409600	t	\N	Sunil	SOUTHERN	200	2026-01-26 22:51:05.357383	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
8d563637-f368-49c4-a851-f7ec5e9116ab	Imaduwa, Sri Lanka	2026-01-26 22:51:59.597228	GALLE	Imaduwa	6.03573600	80.38901510	t	\N	Sunil	SOUTHERN	200	2026-01-26 22:51:59.604342	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
10c8a055-1239-485f-8b44-05897d01354a	Karandeniya, Sri Lanka	2026-01-26 22:52:38.694701	GALLE	Karandeniya	6.27054540	80.09116930	t	\N	Sunil	SOUTHERN	200	2026-01-26 22:52:38.700264	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
9aada6e2-cb21-463c-964d-30d60dcd996d	Galle 80000, Sri Lanka	2026-01-26 22:55:32.104266	GALLE	Kadawassathara	6.04819180	80.21539980	t	\N	Sunil	SOUTHERN	200	2026-01-26 22:55:32.108294	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e1a48126-7589-45df-8c56-b1b21bf58e4c	Nagoda, Sri Lanka	2026-01-26 22:56:06.290245	GALLE	Nagoda	6.19798490	80.27469720	t	\N	Sunil	SOUTHERN	200	2026-01-26 22:56:06.292651	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
fdb70e6e-63a3-4ef4-8d38-982fd4192f34	Niyagama, Sri Lanka	2026-01-26 22:58:06.923304	GALLE	Niyagama	6.30832110	80.24852990	t	\N	Sunil	SOUTHERN	200	2026-01-26 22:58:06.924577	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
71501453-4680-480e-a824-1228a19117c1	Rathgama, Sri Lanka	2026-01-26 22:58:46.595474	GALLE	Rathgama	6.09361870	80.14305420	t	\N	Sunil	SOUTHERN	300	2026-01-26 22:58:46.601342	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
dbcc5c89-680b-431c-a047-685b023578ac	Welivitiya-Divithura, Sri Lanka	2026-01-26 22:59:18.129485	GALLE	Weliwitiya-Divithura	6.23603770	80.15555330	t	\N	Sunil	SOUTHERN	300	2026-01-26 22:59:18.133605	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e2498062-282f-423a-bf4b-da537870f326	Yakkalamulla, Sri Lanka	2026-01-26 22:59:51.151338	GALLE	Yakkalamulla	6.10642640	80.34831450	t	\N	Sunil	SOUTHERN	400	2026-01-26 22:59:51.155702	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
9e50cfae-c5e1-4321-9814-ff7705ca6165	Akuressa, Sri Lanka	2026-01-26 23:16:03.073768	MATARA	Akuressa	6.10050110	80.47758120	t	\N	Sunil	SOUTHERN	300	2026-01-26 23:16:03.082111	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2a502d3d-a3d4-4838-b4bf-73a03ebee775	Athuraliya, Sri Lanka	2026-01-26 23:16:47.143808	MATARA	Athuraliya	6.06833560	80.50059280	t	\N	Sunil	SOUTHERN	300	2026-01-26 23:16:47.143808	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
eaa5cfbc-84d5-417d-8e65-8ce4539ed94e	Deniyaya, Sri Lanka	2026-01-26 23:19:00.266687	MATARA	Deniyaya	6.34248470	80.55965820	t	\N	Sunil	SOUTHERN	3000	2026-01-26 23:19:00.27123	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
1fe10e2d-2ac9-49a8-9daf-2968a0224cab	Dikwella, Sri Lanka	2026-01-26 23:19:46.626488	MATARA	Dikwella	5.97686170	80.69863170	t	\N	Sunil	SOUTHERN	200	2026-01-26 23:19:46.628591	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ca1d0ac8-2c23-402e-8a0e-0ea67af96b8c	Hakmana, Sri Lanka	2026-01-26 23:20:27.405585	MATARA	Hakmana	6.07955390	80.65766230	t	\N	Sunil	SOUTHERN	300	2026-01-26 23:20:27.407587	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
0e56adb6-010f-46eb-8b18-454f9b72195e	Kamburupitiya, Sri Lanka	2026-01-26 23:21:11.95125	MATARA	Kamburupitiya	6.08017500	80.56627120	t	\N	Sunil	SOUTHERN	300	2026-01-26 23:21:11.954249	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
7a4ada46-f8fb-453b-a3df-d24a81466a30	Kekanadura, Sri Lanka	2026-01-26 23:21:59.723252	MATARA	Kekanadura	5.96375280	80.61326460	t	\N	Sunil	SOUTHERN	400	2026-01-26 23:21:59.738783	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a68e5b58-e2b1-40fb-998c-c7100ce6a0cf	Malimbada Junction, Sri Lanka	2026-01-26 23:23:05.233263	MATARA	Malimbada	6.00946250	80.51758920	t	\N	Sunil	SOUTHERN	300	2026-01-26 23:23:05.236217	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
47f10a99-b798-4a25-92de-cfb62f91cdef	Matara, Sri Lanka	2026-01-26 23:23:55.070824	MATARA	Matara	5.94963090	80.54685290	t	\N	Sunil	SOUTHERN	2000	2026-01-26 23:23:55.072823	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
aa7b0400-9511-42d7-b433-aa6b16b34c0b	Mulatiyana, Sri Lanka	2026-01-26 23:24:44.361815	MATARA	Mulatiyana	6.16062450	80.58421530	t	\N	Sunil	SOUTHERN	500	2026-01-26 23:24:44.364812	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
9746bbef-e8e0-4f54-9ca7-bceecff68882	Pasgoda, Sri Lanka	2026-01-26 23:25:40.766709	MATARA	Pasgoda	6.24563370	80.60860210	t	\N	Sunil	SOUTHERN	500	2026-01-26 23:25:40.770225	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a3d949d8-5498-403b-a6df-9c218e1d8360	Pitabeddara, Sri Lanka	2026-01-26 23:26:50.164913	MATARA	Pitabeddara	6.19626350	80.46871100	t	\N	Sunil	SOUTHERN	600	2026-01-26 23:26:50.170601	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
3430251e-238d-4782-b060-fad61b54fe92	Welipitiya, Sri Lanka	2026-01-26 23:27:43.3939	MATARA	Welpitiya	6.00724770	80.44372320	t	\N	Sunil	SOUTHERN	8000	2026-01-26 23:27:43.396867	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
5fd01040-f892-4ca6-af48-9879d873878c	Weligama, Sri Lanka	2026-01-26 23:28:23.333626	MATARA	Weligama	5.97371090	80.42935450	t	\N	Sunil	SOUTHERN	400	2026-01-26 23:28:23.336664	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
cc59bdcf-358e-487c-a688-4a3e3aad1ba8	Ambalantota, Sri Lanka	2026-01-27 09:00:56.447394	HAMBANTOTA	Ambalanthota	6.12259990	81.02375940	t	\N	Nimal	SOUTHERN	200	2026-01-27 09:00:56.473425	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
7e370397-7ee8-41e1-a958-73d80a9516c3	Agunukolapelessa, Sri Lanka	2026-01-27 09:01:31.657594	HAMBANTOTA	Angunakolapelessa	6.16588210	80.89927040	t	\N	Nimal	SOUTHERN	300	2026-01-27 09:01:31.663668	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
4b895154-0e3d-4786-8217-efc12a205551	Beliatta, Sri Lanka	2026-01-27 09:02:39.077816	HAMBANTOTA	Beliaththa	6.04816280	80.73342700	t	\N	Nimal	SOUTHERN	300	2026-01-27 09:02:39.088635	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
120aca55-4b49-4534-b0fb-011eb95dc726	Hambantota, Sri Lanka	2026-01-27 09:03:15.227824	HAMBANTOTA	Hambanthota	6.12594140	81.12477300	t	\N	Nimal	SOUTHERN	300	2026-01-27 09:03:15.2402	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
4ca2cf28-b00c-41e5-8995-6b8fa11dc7e9	Katuwana, Sri Lanka	2026-01-27 09:03:55.43173	HAMBANTOTA	Katuwana	6.26465540	80.69128770	t	\N	Nimal	SOUTHERN	3000	2026-01-27 09:03:55.441733	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
20a6093b-2a69-4b4c-96fb-a8725fb110cc	Lunugamvehera, Sri Lanka	2026-01-27 09:04:50.587754	HAMBANTOTA	Lunugamvehera	6.34756100	81.19644940	t	\N	Nimal	SOUTHERN	200	2026-01-27 09:04:50.594964	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a603971f-eeb9-4e2f-b4c4-66e54f1d0d82	Meegas Ara, Sri Lanka	2026-01-27 09:05:29.344252	HAMBANTOTA	Meegas Ara	6.20200950	80.76855080	t	\N	Nimal	SOUTHERN	300	2026-01-27 09:05:29.351137	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
214ed421-6175-4eb2-bc4f-2f86dbf2f91f	Sooriyawewa, Sri Lanka	2026-01-27 09:06:12.264555	HAMBANTOTA	Sooriyawewa	6.31788610	80.99817970	t	\N	Nimal	SOUTHERN	300	2026-01-27 09:06:12.269013	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
dd71a728-97c7-46fb-b6dd-33b3ef03718b	Tangalle, Sri Lanka	2026-01-27 09:06:44.938292	HAMBANTOTA	Tangalla	6.02854880	80.79465800	t	\N	Nimal	SOUTHERN	300	2026-01-27 09:06:44.943297	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e4b1719b-17f5-4f5c-aee1-f1dad458a73e	Tissamaharama, Sri Lanka	2026-01-27 09:07:11.402292	HAMBANTOTA	Thissamaharama	6.27756600	81.28603150	t	\N	Nimal	SOUTHERN	200	2026-01-27 09:07:11.408494	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
bdfe7eb2-63de-42d3-a743-1c4011c1d27d	Okewela, Sri Lanka	2026-01-27 09:07:38.690662	HAMBANTOTA	Okawela	6.09888560	80.71589770	t	\N	Nimal	SOUTHERN	400	2026-01-27 09:07:38.697776	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
50e96829-c5ce-4e45-a29b-d77720426d1f	Walasmulla, Sri Lanka	2026-01-27 09:08:05.843204	HAMBANTOTA	Walasmulla	6.15088880	80.69371770	t	\N	Nimal	SOUTHERN	300	2026-01-27 09:08:05.849147	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
802cd111-e7bc-4450-8917-277259f014ee	Badulla, Sri Lanka	2026-01-27 09:11:41.134848	BADULLA	Badulla	6.99340090	81.05498150	t	\N	Nimal	UVA	400	2026-01-27 09:11:41.14006	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2a20b470-c5bd-4f22-8a08-cf2b91050bc3	Bandarawela, Sri Lanka	2026-01-27 09:12:39.182142	BADULLA	Bandarawela	6.82587800	80.99815760	t	\N	Nimal	UVA	300	2026-01-27 09:12:39.188391	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
189adc1b-90f6-4be1-991a-f18e6469aa31	Bambarapana, Sri Lanka	2026-01-27 09:13:13.155361	BADULLA	Babarapana	6.98156960	80.95118350	t	\N	Nimal	UVA	300	2026-01-27 09:13:13.161375	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
285bdc01-f1c3-4927-b591-6b90728d76b9	Ella, Sri Lanka	2026-01-27 09:13:43.515063	BADULLA	Ella	6.87313320	81.04910740	t	\N	Nimal	UVA	300	2026-01-27 09:13:43.520657	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
772fe4a0-f2f5-4363-aa36-7ee4720fd43c	Haldummulla, Sri Lanka	2026-01-27 09:14:25.806847	BADULLA	Haldumulla	6.76160490	80.88575270	t	\N	Nimal	UVA	300	2026-01-27 09:14:25.813844	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
dce4ca49-9d79-4802-a272-3dff056d82b9	Ettampitiya, Sri Lanka	2026-01-27 09:14:57.811605	BADULLA	Ettampitiya	6.93645060	80.98779800	t	\N	Nimal	UVA	400	2026-01-27 09:14:57.817683	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e1df13f9-cd23-427c-a4ad-0ff0d7ca1a89	Haputale, Sri Lanka	2026-01-27 09:15:36.463326	BADULLA	Haputale	6.76890080	80.95999960	t	\N	Nimal	UVA	200	2026-01-27 09:15:36.470654	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
37c01067-97d2-405f-84a9-cf50de299aec	Kandaketiya, Sri Lanka	2026-01-27 09:16:26.18662	BADULLA	Kandaketiya	7.21004960	81.01361060	t	\N	Nimal	UVA	300	2026-01-27 09:16:26.193799	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
03485363-a8eb-43d3-b904-0599d1bf7492	Lunugala, Sri Lanka	2026-01-27 09:17:02.237898	BADULLA	Lunugala	7.04036760	81.20167370	t	\N	Nimal	UVA	200	2026-01-27 09:17:02.246453	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a781f555-95cd-4e43-bbef-99389293e514	Mahiyanganaya, Sri Lanka	2026-01-27 09:17:45.927251	BADULLA	Mahiyanganaya	7.33161020	81.00368210	t	\N	Nimal	UVA	200	2026-01-27 09:17:45.931387	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
fd481ab7-f804-4ecf-a683-a21f0c740043	Rideepana, Sri Lanka	2026-01-27 09:18:13.587609	BADULLA	Meegahakiula	7.01864620	81.05639800	t	\N	Nimal	UVA	200	2026-01-27 09:18:13.596555	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2f084dfc-6285-4246-a08e-f5bd7665c2a6	Passara, Sri Lanka	2026-01-27 09:18:45.41092	BADULLA	Passara	6.93490880	81.15269760	t	\N	Nimal	UVA	200	2026-01-27 09:18:45.416994	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
7b8caaf3-7e5b-4b59-9a4e-8efffaafb5ac	Rideemaliyadda, Sri Lanka	2026-01-27 09:19:28.005148	BADULLA	Rideemaliyadda	7.20996320	81.12926030	t	\N	Nimal	UVA	200	2026-01-27 09:19:28.01115	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
0097b421-5403-4e0b-a8ec-69a7761fd044	Soranathota, Sri Lanka	2026-01-27 09:20:09.439438	BADULLA	Soranathota	7.02116920	81.05383910	t	\N	Nimal	UVA	300	2026-01-27 09:20:09.447117	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
6856c8dd-1cb7-40d7-8ffe-bdbcbedcf416	Uva Paranagama, Sri Lanka	2026-01-27 09:20:39.242627	BADULLA	Uva Paranagama	6.94673110	80.88915140	t	\N	Nimal	UVA	200	2026-01-27 09:20:39.246622	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
23d808fc-8f8e-4152-a512-9244bf56cf99	Girandurukotte, Sri Lanka	2026-01-27 09:21:05.336294	BADULLA	Giradurukotte	7.46291690	81.01749170	t	\N	Nimal	UVA	200	2026-01-27 09:21:05.338338	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
60725f5d-5a8c-424a-8d8e-241040bda667	Welimada, Sri Lanka	2026-01-27 09:21:49.644769	BADULLA	Welimada	6.90495090	80.91178160	t	\N	Nimal	UVA	200	2026-01-27 09:21:49.650777	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
6a7ee9fe-8a4b-4971-9604-c6d448170d0b	Badalkumbura, Sri Lanka	2026-01-27 09:26:17.843334	MONARAGALA	Badalkumbura	6.89559920	81.23635890	t	\N	Nimal	UVA	300	2026-01-27 09:26:17.851353	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
f81d04d6-8c05-4f73-8641-ca398615d2f6	Bibile, Sri Lanka	2026-01-27 09:26:52.621408	MONARAGALA	Bibile	7.15550210	81.22407170	t	\N	Nimal	UVA	200	2026-01-27 09:26:52.629927	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
45dde3c5-6de6-48b6-aec5-32fead5008a3	Buttala, Sri Lanka	2026-01-27 09:27:28.181996	MONARAGALA	Buttala	6.76026760	81.24703990	t	\N	Nimal	UVA	200	2026-01-27 09:27:28.184996	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
98c1b464-e1b5-40d1-b3e4-fdc95771d2fe	Kataragama, Sri Lanka	2026-01-27 09:27:58.774308	MONARAGALA	Katharagama	6.41354630	81.33256790	t	\N	Nimal	UVA	200	2026-01-27 09:27:58.778309	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
1f833e27-a4a0-4af2-8e94-5538e8ec1e18	Madulla, Sri Lanka	2026-01-27 09:28:37.210236	MONARAGALA	Madulla	6.97834270	81.37141060	t	\N	Nimal	UVA	200	2026-01-27 09:28:37.217254	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
8f6aeebb-67a0-484e-a643-71dc89a5f9b7	Medagama, Sri Lanka	2026-01-27 09:29:16.267375	MONARAGALA	Medagama	7.03447320	81.27600400	t	\N	Nimal	UVA	200	2026-01-27 09:29:16.271376	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2dcafc1f-e9de-4b1b-878d-ed7f327dc99e	Monaragala, Sri Lanka	2026-01-27 09:30:06.407101	MONARAGALA	Monaragala	6.87284200	81.35061900	t	\N	Nimal	UVA	200	2026-01-27 09:30:06.413084	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
f3185df8-bda7-483d-9845-d1d916edae87	Sevanagala, Sri Lanka	2026-01-27 09:30:43.891845	MONARAGALA	Sevanagala	6.36693040	80.91924190	t	\N	Nimal	UVA	200	2026-01-27 09:30:43.894846	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e4d49eab-07bc-4952-91e7-4d4df0e96e76	Siyambalanduwa, Sri Lanka	2026-01-27 09:31:45.183028	MONARAGALA	Siyambalanduwa	6.90675940	81.55932290	t	\N	Nimal	UVA	200	2026-01-27 09:31:45.187033	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
5d76c756-01a4-4b76-b0c6-1e13d3bfb751	Thanamalwila, Sri Lanka	2026-01-27 09:32:13.658042	MONARAGALA	Thanamalwila	6.43489970	81.13085460	t	\N	Nimal	UVA	200	2026-01-27 09:32:13.662044	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ebe20932-f21a-402c-85f3-a86b8bb52ef2	Okkampitiya, Sri Lanka	2026-01-27 09:32:46.307485	MONARAGALA	Okkampitiya	6.75003500	81.30761930	t	\N	Nimal	UVA	200	2026-01-27 09:32:46.310557	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
2f1a533e-d36f-4be1-a383-b263da195104	Ethimale, Sri Lanka	2026-01-27 09:33:17.87401	MONARAGALA	Ethimale	6.82372070	81.53571740	t	\N	Nimal	UVA	300	2026-01-27 09:33:17.879427	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
610dc812-3e6f-4c86-b699-a07e4f438edd	Thelulla, Sri Lanka	2026-01-27 09:33:52.641935	MONARAGALA	Thelulla	6.59021490	81.13615430	t	\N	Nimal	UVA	200	2026-01-27 09:33:52.642938	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
038ecca3-1d55-4e68-9054-78b3950fc8bb	Wellawaya, Sri Lanka	2026-01-27 09:34:17.052964	MONARAGALA	Wellawaya	6.73773560	81.10305730	t	\N	Nimal	UVA	200	2026-01-27 09:34:17.056968	7	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e8d6cd54-6526-44da-9b37-6b12cf9e0c98	Udahamulla, Nugegoda, Sri Lanka	2026-01-27 09:40:39.845572	COLOMBO	Colombo (Udahamulla)	6.86655590	79.91507240	t	\N	Sunil	WESTERN	200	2026-01-27 09:40:39.854817	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
3c22e63c-40a1-4aba-ad82-178f13697461	Homagama, Sri Lanka	2026-01-27 09:41:16.29019	COLOMBO	Homagama	6.84557190	80.00357490	t	\N	Sunil	WESTERN	200	2026-01-27 09:41:16.29443	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
161b72c5-c773-4e80-a7d0-f9a98f33247a	Kaduwela, Sri Lanka	2026-01-27 09:41:48.39399	COLOMBO	Kaduwela	6.92906100	79.98277540	t	\N	Sunil	WESTERN	200	2026-01-27 09:41:48.398991	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
4fa87a14-3741-459c-b020-25c89425838a	Kolonnawa, Sri Lanka	2026-01-27 09:42:54.000912	COLOMBO	Kolonnawa	6.92843150	79.89517340	t	\N	Sunil	WESTERN	500	2026-01-27 09:42:54.006393	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
c3c56cc8-2307-4f7e-841c-ebb8f09c114c	Kosgama, Sri Lanka	2026-01-27 09:43:26.163148	COLOMBO	Kosgama	6.93971650	80.13692680	t	\N	Sunil	WESTERN	3000	2026-01-27 09:43:26.167148	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
29529a6d-d69f-4dba-8568-655e1d1b35c8	Moratuwa, Sri Lanka	2026-01-27 09:43:51.731255	COLOMBO	Moratuwa	6.78793040	79.88511420	t	\N	Sunil	WESTERN	300	2026-01-27 09:43:51.738543	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
6e5e7cf6-71a2-4078-968e-93fb1b40aa9c	Padukka, Sri Lanka	2026-01-27 09:44:17.995624	COLOMBO	Padukka	6.84306540	80.09168700	t	\N	Sunil	WESTERN	300	2026-01-27 09:44:18.000626	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
35298b94-9699-4d03-a429-b8447dfe2f07	Dompe, Sri Lanka	2026-01-27 09:50:36.940458	GAMPAHA	Dompe	6.94039470	80.07719620	t	\N	Sunil	WESTERN	300	2026-01-27 09:50:36.947469	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a279bfd5-584e-4658-b382-037543618264	Kesbewa, Piliyandala, Sri Lanka	2026-01-27 09:42:23.284045	COLOMBO	Kesbawa	6.77867010	79.94725020	t	\N	Sunil	WESTERN	200	2026-01-27 09:42:23.288555	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
7256355d-3afb-4677-b680-0237114987c9	Attanagalla, Sri Lanka	2026-01-27 09:49:24.259506	GAMPAHA	Attanagalla	7.11225720	80.13626060	t	\N	Sunil	WESTERN	500	2026-01-27 09:49:24.270015	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
5e2c12f0-e141-444e-8365-763135335af7	Biyagama, Sri Lanka	2026-01-27 09:49:58.912033	GAMPAHA	Biyagama	6.94621530	79.98920340	t	\N	Sunil	WESTERN	300	2026-01-27 09:49:58.919048	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
0858eb59-41d7-4828-8174-7709b36a91fb	Divulapitiya, Sri Lanka	2026-01-27 09:51:15.584833	GAMPAHA	Devulapitiya	7.22456760	80.01946150	t	\N	Sunil	WESTERN	200	2026-01-27 09:51:15.586827	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
f280ab86-5bdc-43d0-b095-3394eeb7e3c8	Gampaha, Sri Lanka	2026-01-27 09:51:43.928914	GAMPAHA	Gampaha	7.09153580	79.99477620	t	\N	Sunil	WESTERN	300	2026-01-27 09:51:43.933032	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
72fe80e2-174b-48f8-a307-ae74c6ff1e87	XWRQ+3CX, Kandy - Colombo Rd, Kadawatha, Sri Lanka	2026-01-27 09:52:32.16097	GAMPAHA	Kadawatha (Kelaniya)	6.99023320	79.93860510	t	\N	Sunil	WESTERN	400	2026-01-27 09:52:32.171468	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
cecdc323-40dc-42aa-969b-78d84320098c	Katana, Sri Lanka	2026-01-27 09:53:02.174696	GAMPAHA	Katana	7.24802840	79.89936650	t	\N	Sunil	WESTERN	300	2026-01-27 09:53:02.182111	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
12e765f2-8c69-4cef-886b-b0a867b79ec8	Mahara, Kadawatha, Sri Lanka	2026-01-27 09:53:31.832981	GAMPAHA	Mahara	6.99086610	79.93955660	t	\N	Sunil	WESTERN	300	2026-01-27 09:53:31.841337	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
7cbb5cf9-67c2-4e8e-ad91-b7c59220d1f8	Marandagahamula, Sri Lanka	2026-01-27 09:54:00.452662	GAMPAHA	Maradagahamula	7.22898140	79.99060150	t	\N	Sunil	WESTERN	200	2026-01-27 09:54:00.458644	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
c5a72793-9023-4d29-8a9a-56a37b3412da	Minuwangoda, Sri Lanka	2026-01-27 09:54:25.326705	GAMPAHA	Minuwangoda	7.16897380	79.94806080	t	\N	Sunil	WESTERN	300	2026-01-27 09:54:25.329698	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
6f015b87-3087-47e6-8919-c5cd3a466126	Mirigama, Sri Lanka	2026-01-27 09:54:53.465757	GAMPAHA	Meerigama	7.24746110	80.12950790	t	\N	Sunil	WESTERN	200	2026-01-27 09:54:53.47316	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
510ecdee-f493-4e98-9dae-a6678b90b799	Negombo, Sri Lanka	2026-01-27 09:55:30.052899	GAMPAHA	Negambo	7.20552080	79.85125620	t	\N	Sunil	WESTERN	200	2026-01-27 09:55:30.057853	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
1fdc92f3-8e12-4cc6-bf29-6daf73757477	Welisara, Sri Lanka	2026-01-27 09:55:57.954362	GAMPAHA	Welisara	7.02775590	79.89827930	t	\N	Sunil	WESTERN	300	2026-01-27 09:55:57.961086	5	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
a7957b0f-359f-4fa1-ba2b-09edc83676e2	Baduraliya, Sri Lanka	2026-01-27 09:59:39.177874	KALUTARA	Baduraliya	6.51712210	80.23118010	t	\N	Kamal	WESTERN	200	2026-01-27 09:59:39.192017	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
eec05e51-9610-4952-b17f-a4fc52b0a812	Bandaragama, Sri Lanka	2026-01-27 10:00:05.435698	KALUTARA	Bandaragama	6.71440660	79.98898240	t	\N	Kamal	WESTERN	200	2026-01-27 10:00:05.447048	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
75b3ab93-ab11-4a4f-b2e5-adda2d2085f0	Beruwala, Sri Lanka	2026-01-27 10:00:34.160893	KALUTARA	Beruwala	6.47592350	79.98414370	t	\N	Kamal	WESTERN	200	2026-01-27 10:00:34.174158	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e90fbca4-0f86-41f0-8502-dce905da33b4	Bulathsinhala, Sri Lanka	2026-01-27 10:01:06.324823	KALUTARA	Bulathsinhala	6.64961360	80.17802240	t	\N	Kamal	WESTERN	200	2026-01-27 10:01:06.32899	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
e03f4312-3892-4286-a78b-c29b07173394	Dodangoda, Sri Lanka	2026-01-27 10:01:31.903631	KALUTARA	Dodangoda	6.54203710	80.04020440	t	\N	Kamal	WESTERN	300	2026-01-27 10:01:31.910185	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
307fa15b-0ad5-4484-b9be-e150b6b7bf28	Horana, Sri Lanka	2026-01-27 10:02:08.942796	KALUTARA	Horana	6.72298060	80.06466820	t	\N	Kamal	WESTERN	300	2026-01-27 10:02:08.946804	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
870f7938-0606-4736-a7d0-f2b2768e12cf	Ingiriya, Sri Lanka	2026-01-27 10:02:46.680712	KALUTARA	Ingiriya	6.74385960	80.17669560	t	\N	Kamal	WESTERN	500	2026-01-27 10:02:46.687087	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
b219eefd-292b-4950-ba07-ad7648f4204e	Kalutara, Sri Lanka	2026-01-27 10:03:26.253026	KALUTARA	Kalutara	6.58539480	79.96074000	t	\N	Kamal	WESTERN	200	2026-01-27 10:03:26.257027	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
ac261fac-5416-4838-ba08-0eff8e8a627f	Matugama, Sri Lanka	2026-01-27 10:03:55.702337	KALUTARA	Matugama	6.52285500	80.11422380	t	\N	Kamal	WESTERN	300	2026-01-27 10:03:55.70485	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
5edfd9dd-38de-468e-8ef1-d3f1041a86dc	Millaniya, Sri Lanka	2026-01-27 10:04:42.876895	KALUTARA	Millaniya	6.68005660	80.02070400	t	\N	Kamal	WESTERN	300	2026-01-27 10:04:42.880897	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
9a1f0fce-6082-49e1-b060-4ab6f086a65d	Panadura, Sri Lanka	2026-01-27 10:05:11.129974	KALUTARA	Panadura	6.71063610	79.90742620	t	\N	Kamal	WESTERN	200	2026-01-27 10:05:11.135317	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
759119d9-90c6-4b2d-9c4a-c5cd81b5f41f	Walallawita, Sri Lanka	2026-01-27 10:05:39.769933	KALUTARA	Walallavita	6.37696710	80.19579410	t	\N	Kamal	WESTERN	200	2026-01-27 10:05:39.776531	9	86b9c215-fe06-491a-bc09-5a4f37a47f11	\N	Farms raising chickens, ducks, turkeys, or other birds for eggs or meat
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (id, active, created_at, email, first_name, last_login, last_name, password, phone_number, role, updated_at, username, district, province) FROM stdin;
1	t	2025-10-28 13:39:30.899483	admin@adrs.com	System	2025-10-28 21:06:03.882576	Administrator	$2a$10$.jQ4luc8o9T/HGQutUSzuOq/Ft.wZ8ku.S91Z./3xcYSV2t//2dbu	+94771234567	ADMIN	2025-10-28 21:06:03.920526	admin	\N	\N
6	t	2025-10-31 10:46:34.382927	anuradha@gmail.com	Anuradha	\N	Kumara	$2a$10$BDjoqwPbYetcMZlYk2jBXONMQ0WwFzybPdiN5bQXQ6xuhyMfqjfh2	0783476519	VETERINARY_OFFICER	2025-10-31 10:46:34.382927	Anuradha	ANURADHAPURA	NORTH_CENTRAL
10	t	2025-11-18 10:42:35.905828	sadun123@gmail.com	Sadun	\N	Bandara	$2a$10$nCcQVnFvsyyu62v8jxlBuesR4glT5jOQ6Gnj2MuAgtqjrYx5ENiNW	0872538756	VETERINARY_OFFICER	2025-11-18 10:42:35.905828	Sadun	BADULLA	UVA
11	t	2025-11-18 10:44:55.458528	kanishka@gmail.com	Kanishka	\N	Udapitiya	$2a$10$NkeITfDOoYfj1Hag6DWSfeozUsLXu6bF6qbcWmef4MB4c5JupO1eK	0786072746	VETERINARY_OFFICER	2025-11-18 10:44:55.458528	Kanishka	NUWARA_ELIYA	CENTRAL
12	t	2025-11-18 10:47:35.905711	sasanka@gmail.com	Sasanka	\N	Silva	$2a$10$Rpw0HVeI3bUtbOyTGnr0peQ4BHZmKsf.eGPkynBwbaER7bj328lbq	0842797845	ADMIN	2025-11-18 10:47:35.905711	Sasanka	COLOMBO	WESTERN
5	t	2025-10-30 23:30:36.440226	sunil@gmail.com	Sunil	\N	Jayasinghe	$2a$10$usBHoip/birQuCASCF6S5Oa0SWibb3Ijzg7iorfiVzCTrxmcsY0Sy	0723478956	VETERINARY_OFFICER	2025-12-09 00:25:30.933236	sunil	KANDY	CENTRAL
7	t	2025-10-31 11:01:48.267324	nimal@gmail.com	Nimal	\N	Silva	$2a$10$rY0ioccRcU8AccWGRlIT3e/n5ZZgAk8F1Xsl2IZuBeQhXD76Ykwbi	0783423478	VETERINARY_OFFICER	2025-12-18 15:23:19.363436	nimal	COLOMBO	WESTERN
9	t	2025-11-18 10:40:38.513263	kamal@gmail.com	Kamal	\N	Gunawardhana	$2a$10$YLuys8U5W1/TYhR7vyGBi.3eoQkTlFRwTY4ZrQPT84v6F67yB8GFq	0981236285	VETERINARY_OFFICER	2026-01-26 18:18:55.065549	kamal	ANURADHAPURA	NORTH_CENTRAL
\.


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.users_id_seq', 13, true);


--
-- Name: animal_types animal_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.animal_types
    ADD CONSTRAINT animal_types_pkey PRIMARY KEY (id);


--
-- Name: animals animals_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.animals
    ADD CONSTRAINT animals_pkey PRIMARY KEY (id);


--
-- Name: disease_animal_types disease_animal_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.disease_animal_types
    ADD CONSTRAINT disease_animal_types_pkey PRIMARY KEY (disease_id, animal_type_id);


--
-- Name: disease_reports disease_reports_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.disease_reports
    ADD CONSTRAINT disease_reports_pkey PRIMARY KEY (id);


--
-- Name: diseases diseases_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.diseases
    ADD CONSTRAINT diseases_pkey PRIMARY KEY (id);


--
-- Name: farm_animals farm_animals_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.farm_animals
    ADD CONSTRAINT farm_animals_pkey PRIMARY KEY (id);


--
-- Name: farm_types farm_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.farm_types
    ADD CONSTRAINT farm_types_pkey PRIMARY KEY (id);


--
-- Name: farms farms_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.farms
    ADD CONSTRAINT farms_pkey PRIMARY KEY (id);


--
-- Name: farm_animals uk67plxesk96xo1ai9llc7if92d; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.farm_animals
    ADD CONSTRAINT uk67plxesk96xo1ai9llc7if92d UNIQUE (farm_id, animal_type_id);


--
-- Name: animals uk_2im093ymp3n2otcoxhbkolp5m; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.animals
    ADD CONSTRAINT uk_2im093ymp3n2otcoxhbkolp5m UNIQUE (tag_number);


--
-- Name: farm_types uk_3pgux1gpdosrwjsjnyvdovql2; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.farm_types
    ADD CONSTRAINT uk_3pgux1gpdosrwjsjnyvdovql2 UNIQUE (type_name);


--
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: animal_types uk_bcgod8pi56rruf5v0bnn47fka; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.animal_types
    ADD CONSTRAINT uk_bcgod8pi56rruf5v0bnn47fka UNIQUE (type_name);


--
-- Name: diseases uk_bm131kf3u9eked5f3djb21cyv; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.diseases
    ADD CONSTRAINT uk_bm131kf3u9eked5f3djb21cyv UNIQUE (disease_name);


--
-- Name: diseases uk_o0n4a5e38eq3b2lbyfl0x1o8k; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.diseases
    ADD CONSTRAINT uk_o0n4a5e38eq3b2lbyfl0x1o8k UNIQUE (disease_code);


--
-- Name: users uk_r43af9ap4edm43mmtq01oddj6; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_r43af9ap4edm43mmtq01oddj6 UNIQUE (username);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: idx_diseases_animal_type_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_diseases_animal_type_id ON public.diseases USING btree (animal_type_id);


--
-- Name: idx_diseases_created_by_vet; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_diseases_created_by_vet ON public.diseases USING btree (created_by_vet);


--
-- Name: diseases fk1vlcw6adjkkwx3q2fl0ggjl1c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.diseases
    ADD CONSTRAINT fk1vlcw6adjkkwx3q2fl0ggjl1c FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: disease_reports fk2jtn2xqc1mtdtjsv504csof1c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.disease_reports
    ADD CONSTRAINT fk2jtn2xqc1mtdtjsv504csof1c FOREIGN KEY (confirmed_by) REFERENCES public.users(id);


--
-- Name: animals fk4jtqu92829064kyr7msrd54gs; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.animals
    ADD CONSTRAINT fk4jtqu92829064kyr7msrd54gs FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: disease_animal_types fk4vs5jgfhkaeltsd3vic5pdd45; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.disease_animal_types
    ADD CONSTRAINT fk4vs5jgfhkaeltsd3vic5pdd45 FOREIGN KEY (animal_type_id) REFERENCES public.animal_types(id);


--
-- Name: animals fk5vemm94li86pbeuesqmddiefm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.animals
    ADD CONSTRAINT fk5vemm94li86pbeuesqmddiefm FOREIGN KEY (animal_type_id) REFERENCES public.animal_types(id);


--
-- Name: farm_types fk64hy51khtny1ql5252wf4qdw7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.farm_types
    ADD CONSTRAINT fk64hy51khtny1ql5252wf4qdw7 FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: farm_animals fk81hbc5fdk4vuv52wvjjiaobl1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.farm_animals
    ADD CONSTRAINT fk81hbc5fdk4vuv52wvjjiaobl1 FOREIGN KEY (farm_id) REFERENCES public.farms(id);


--
-- Name: diseases fk_disease_animal_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.diseases
    ADD CONSTRAINT fk_disease_animal_type FOREIGN KEY (animal_type_id) REFERENCES public.animal_types(id) ON DELETE SET NULL;


--
-- Name: disease_reports fkb5t5i52u6385kfqxjo1v0ivqh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.disease_reports
    ADD CONSTRAINT fkb5t5i52u6385kfqxjo1v0ivqh FOREIGN KEY (farm_id) REFERENCES public.farms(id);


--
-- Name: disease_reports fkbybp15lg8knej4jug54n3jyn4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.disease_reports
    ADD CONSTRAINT fkbybp15lg8knej4jug54n3jyn4 FOREIGN KEY (reported_by) REFERENCES public.users(id);


--
-- Name: animals fkdwalxt39no0gs365bab0gbclm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.animals
    ADD CONSTRAINT fkdwalxt39no0gs365bab0gbclm FOREIGN KEY (farm_id) REFERENCES public.farms(id);


--
-- Name: diseases fkfbaw08r44m9cm55hm7n0v98jy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.diseases
    ADD CONSTRAINT fkfbaw08r44m9cm55hm7n0v98jy FOREIGN KEY (animal_type_id) REFERENCES public.animal_types(id);


--
-- Name: disease_reports fkgv74o98a0ahfmwe55dx2t145e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.disease_reports
    ADD CONSTRAINT fkgv74o98a0ahfmwe55dx2t145e FOREIGN KEY (disease_id) REFERENCES public.diseases(id);


--
-- Name: farms fkj97tvxp1u971tjv7utmgj36hj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.farms
    ADD CONSTRAINT fkj97tvxp1u971tjv7utmgj36hj FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: disease_animal_types fkljqe9iyooh4cxiawnskj37t4q; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.disease_animal_types
    ADD CONSTRAINT fkljqe9iyooh4cxiawnskj37t4q FOREIGN KEY (disease_id) REFERENCES public.diseases(id);


--
-- Name: farm_types fkmoqknithj5rkiiawgr55u92xd; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.farm_types
    ADD CONSTRAINT fkmoqknithj5rkiiawgr55u92xd FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: animals fkog9lqxya5jdqupea8xvufu48h; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.animals
    ADD CONSTRAINT fkog9lqxya5jdqupea8xvufu48h FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: farms fkpx2fcwjciocvl25n02xvs87br; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.farms
    ADD CONSTRAINT fkpx2fcwjciocvl25n02xvs87br FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: farm_animals fkqmqdflwj7hugb9x44nc9kctm3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.farm_animals
    ADD CONSTRAINT fkqmqdflwj7hugb9x44nc9kctm3 FOREIGN KEY (animal_type_id) REFERENCES public.animal_types(id);


--
-- Name: farms fkqrp2wwlmvdq71x2u64i351io2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.farms
    ADD CONSTRAINT fkqrp2wwlmvdq71x2u64i351io2 FOREIGN KEY (farm_type_id) REFERENCES public.farm_types(id);


--
-- Name: diseases fkt5l8ft5ed3icvqtvj76taebqa; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.diseases
    ADD CONSTRAINT fkt5l8ft5ed3icvqtvj76taebqa FOREIGN KEY (updated_by) REFERENCES public.users(id);


--
-- Name: disease_reports fktjbddpuighexq01b7d91gn75p; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.disease_reports
    ADD CONSTRAINT fktjbddpuighexq01b7d91gn75p FOREIGN KEY (animal_type_id) REFERENCES public.animal_types(id);


--
-- PostgreSQL database dump complete
--

\unrestrict D0W9DvSNYA0oquSLa3P7zKmdqxyyfPb3lutZc0RL1QBMWwFhWCVrBnLCidZdj4k


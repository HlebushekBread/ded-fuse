--
-- PostgreSQL database dump
--

-- Dumped from database version 18.2
-- Dumped by pg_dump version 18.2

-- Started on 2026-03-27 20:56:05

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
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
-- TOC entry 228 (class 1259 OID 25446)
-- Name: heartbeat_log; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.heartbeat_log (
    id bigint CONSTRAINT heartbeat_logs_id_not_null NOT NULL,
    user_id bigint CONSTRAINT heartbeat_logs_user_id_not_null NOT NULL,
    tapped_at timestamp without time zone NOT NULL,
    lat double precision,
    lon double precision
);


--
-- TOC entry 227 (class 1259 OID 25445)
-- Name: heartbeat_logs_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.heartbeat_log ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.heartbeat_logs_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 224 (class 1259 OID 25421)
-- Name: push_token; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.push_token (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    token character varying NOT NULL,
    platform integer NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- TOC entry 223 (class 1259 OID 25420)
-- Name: push_token_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.push_token ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.push_token_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 222 (class 1259 OID 25390)
-- Name: role; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.role (
    id bigint NOT NULL,
    name character varying NOT NULL
);


--
-- TOC entry 225 (class 1259 OID 25433)
-- Name: trusted_contact; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.trusted_contact (
    id bigint NOT NULL,
    keeper_id bigint NOT NULL,
    member_id bigint NOT NULL,
    status integer NOT NULL,
    created_at timestamp without time zone NOT NULL,
    responded_at timestamp without time zone
);


--
-- TOC entry 226 (class 1259 OID 25436)
-- Name: trusted_contact_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.trusted_contact ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.trusted_contact_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 220 (class 1259 OID 25378)
-- Name: user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."user" (
    id bigint NOT NULL,
    phone_number character varying CONSTRAINT user_username_not_null NOT NULL,
    full_name character varying,
    role_id bigint NOT NULL,
    last_known_lat double precision,
    last_known_lon double precision,
    last_known_at timestamp without time zone NOT NULL,
    last_heartbeat_at timestamp without time zone NOT NULL,
    reminder_sent_at timestamp without time zone NOT NULL,
    last_active_at timestamp without time zone NOT NULL,
    registered_at timestamp without time zone NOT NULL
);


--
-- TOC entry 221 (class 1259 OID 25387)
-- Name: user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public."user" ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.user_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 5047 (class 0 OID 25446)
-- Dependencies: 228
-- Data for Name: heartbeat_log; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.heartbeat_log (id, user_id, tapped_at, lat, lon) FROM stdin;
\.


--
-- TOC entry 5043 (class 0 OID 25421)
-- Dependencies: 224
-- Data for Name: push_token; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.push_token (id, user_id, token, platform, updated_at) FROM stdin;
\.


--
-- TOC entry 5041 (class 0 OID 25390)
-- Dependencies: 222
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.role (id, name) FROM stdin;
1	MEMBER
2	KEEPER
\.


--
-- TOC entry 5044 (class 0 OID 25433)
-- Dependencies: 225
-- Data for Name: trusted_contact; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.trusted_contact (id, keeper_id, member_id, status, created_at, responded_at) FROM stdin;
\.


--
-- TOC entry 5039 (class 0 OID 25378)
-- Dependencies: 220
-- Data for Name: user; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public."user" (id, phone_number, role_id, last_known_lat, last_known_lon, last_known_at, last_heartbeat_at, reminder_sent_at, last_active_at, registered_at) FROM stdin;
\.


--
-- TOC entry 5053 (class 0 OID 0)
-- Dependencies: 227
-- Name: heartbeat_logs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.heartbeat_logs_id_seq', 1, false);


--
-- TOC entry 5054 (class 0 OID 0)
-- Dependencies: 223
-- Name: push_token_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.push_token_id_seq', 1, false);


--
-- TOC entry 5055 (class 0 OID 0)
-- Dependencies: 226
-- Name: trusted_contact_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.trusted_contact_id_seq', 1, false);


--
-- TOC entry 5056 (class 0 OID 0)
-- Dependencies: 221
-- Name: user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.user_id_seq', 1, false);


--
-- TOC entry 4890 (class 2606 OID 25453)
-- Name: heartbeat_log heartbeat_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.heartbeat_log
    ADD CONSTRAINT heartbeat_logs_pkey PRIMARY KEY (id);


--
-- TOC entry 4888 (class 2606 OID 25432)
-- Name: push_token push_token_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.push_token
    ADD CONSTRAINT push_token_pkey PRIMARY KEY (id);


--
-- TOC entry 4886 (class 2606 OID 25398)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 4884 (class 2606 OID 25389)
-- Name: user user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


-- Completed on 2026-03-27 20:56:05

--
-- PostgreSQL database dump complete
--


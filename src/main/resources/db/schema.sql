-- public.links definition

-- Drop table

-- DROP TABLE public.links;

CREATE TABLE public.links (
	id uuid NOT NULL,
	confirmed_at timestamp(6) NULL,
	created_at timestamp(6) NOT NULL,
	host_id uuid NOT NULL,
	protected_id uuid NOT NULL,
	status varchar(20) NOT NULL,
	updated_at timestamp(6) NOT NULL,
	CONSTRAINT links_pkey PRIMARY KEY (id),
	CONSTRAINT links_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'ACTIVE'::character varying, 'REJECTED'::character varying, 'CANCELLED'::character varying])::text[]))),
	CONSTRAINT uk_link_host_protected_active UNIQUE (host_id, protected_id, status)
);


-- public.users definition

-- Drop table

-- DROP TABLE public.users;

CREATE TABLE public.users (
	id uuid NOT NULL,
	active bool NOT NULL,
	created_at timestamp(6) NOT NULL,
	email varchar(255) NOT NULL,
	"name" varchar(100) NOT NULL,
	phone varchar(20) NOT NULL,
	CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (id)
);


-- public.alerts definition

-- Drop table

-- DROP TABLE public.alerts;

CREATE TABLE public.alerts (
	id uuid NOT NULL,
	created_at timestamp(6) NOT NULL,
	link_id uuid NOT NULL,
	protected_user_id uuid NOT NULL,
	reason varchar(500) NULL,
	resolution_note varchar(500) NULL,
	resolved_at timestamp(6) NULL,
	resolved_by_user_id uuid NULL,
	status varchar(20) NOT NULL,
	suspicious_url varchar(2048) NOT NULL,
	CONSTRAINT alerts_pkey PRIMARY KEY (id),
	CONSTRAINT alerts_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'RESOLVED_SAFE'::character varying, 'RESOLVED_BLOCKED'::character varying])::text[]))),
	CONSTRAINT fk70n2dhnl5rvxeca9he7ej2saw FOREIGN KEY (link_id) REFERENCES public.links(id),
	CONSTRAINT fk8bvp8gqr0stgd6osbl8g5x96p FOREIGN KEY (resolved_by_user_id) REFERENCES public.users(id),
	CONSTRAINT fkm6wras2q4cghl22scayoues7l FOREIGN KEY (protected_user_id) REFERENCES public.users(id)
);
CREATE INDEX idx_alert_link_id ON public.alerts USING btree (link_id);
CREATE INDEX idx_alert_protected_user ON public.alerts USING btree (protected_user_id);
CREATE INDEX idx_alert_status ON public.alerts USING btree (status);


-- public.device_tokens definition

-- Drop table

-- DROP TABLE public.device_tokens;

CREATE TABLE public.device_tokens (
	id uuid NOT NULL,
	created_at timestamp(6) NOT NULL,
	platform varchar(32) NOT NULL,
	"token" varchar(512) NOT NULL,
	updated_at timestamp(6) NOT NULL,
	user_id uuid NOT NULL,
	CONSTRAINT device_tokens_pkey PRIMARY KEY (id),
	CONSTRAINT idx_device_token_token UNIQUE (token),
	CONSTRAINT fkhc7d11bnr8x9gs5biohdhnx1c FOREIGN KEY (user_id) REFERENCES public.users(id)
);
CREATE INDEX idx_device_token_user ON public.device_tokens USING btree (user_id);


-- public.emergency_alerts definition

-- Drop table

-- DROP TABLE public.emergency_alerts;

CREATE TABLE public.emergency_alerts (
	id uuid NOT NULL,
	created_at timestamp(6) NOT NULL,
	latitude float8 NOT NULL,
	link_id uuid NOT NULL,
	longitude float8 NOT NULL,
	primary_host_user_id uuid NOT NULL,
	protected_user_id uuid NOT NULL,
	resolution_note varchar(500) NULL,
	resolution_type varchar(30) NULL,
	resolved_at timestamp(6) NULL,
	resolved_by_user_id uuid NULL,
	status varchar(20) NOT NULL,
	CONSTRAINT emergency_alerts_pkey PRIMARY KEY (id),
	CONSTRAINT emergency_alerts_resolution_type_check CHECK (((resolution_type)::text = ANY ((ARRAY['FALSE_ALARM'::character varying, 'ALL_SAFE'::character varying, 'POLICE_SENT'::character varying])::text[]))),
	CONSTRAINT emergency_alerts_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'RESOLVED'::character varying])::text[]))),
	CONSTRAINT fk2639vjvq7an84urstd9gb8h6l FOREIGN KEY (resolved_by_user_id) REFERENCES public.users(id),
	CONSTRAINT fkbsvhcjd8nlql9ttroj2ra44a7 FOREIGN KEY (protected_user_id) REFERENCES public.users(id),
	CONSTRAINT fko42p88msah64w0i53tch0bodm FOREIGN KEY (link_id) REFERENCES public.links(id),
	CONSTRAINT fkq77lcfnn9yvvd2n0y3mg7up5m FOREIGN KEY (primary_host_user_id) REFERENCES public.users(id)
);
CREATE INDEX idx_emergency_host_status ON public.emergency_alerts USING btree (primary_host_user_id, status);
CREATE INDEX idx_emergency_link_id ON public.emergency_alerts USING btree (link_id);
CREATE INDEX idx_emergency_protected ON public.emergency_alerts USING btree (protected_user_id);


-- public.emergency_audio_recordings definition

-- Drop table

-- DROP TABLE public.emergency_audio_recordings;

CREATE TABLE public.emergency_audio_recordings (
	id uuid NOT NULL,
	created_at timestamp(6) NOT NULL,
	duration_seconds int4 NULL,
	emergency_alert_id uuid NOT NULL,
	file_size_bytes int8 NULL,
	playback_url varchar(2048) NULL,
	status varchar(20) NOT NULL,
	storage_file_id varchar(255) NULL,
	storage_provider varchar(30) NOT NULL,
	uploaded_at timestamp(6) NULL,
	CONSTRAINT emergency_audio_recordings_pkey PRIMARY KEY (id),
	CONSTRAINT emergency_audio_recordings_status_check CHECK (((status)::text = ANY ((ARRAY['RECORDING'::character varying, 'UPLOADED'::character varying, 'FAILED'::character varying])::text[]))),
	CONSTRAINT emergency_audio_recordings_storage_provider_check CHECK (((storage_provider)::text = ANY ((ARRAY['GOOGLE_DRIVE'::character varying, 'LOCAL'::character varying])::text[]))),
	CONSTRAINT fk7662oqjkfke3rqawpavm56cve FOREIGN KEY (emergency_alert_id) REFERENCES public.emergency_alerts(id)
);
CREATE INDEX idx_emergency_audio_alert ON public.emergency_audio_recordings USING btree (emergency_alert_id, created_at);


-- public.family_groups definition

-- Drop table

-- DROP TABLE public.family_groups;

CREATE TABLE public.family_groups (
	id uuid NOT NULL,
	created_at timestamp(6) NOT NULL,
	"name" varchar(100) NOT NULL,
	primary_host_user_id uuid NOT NULL,
	CONSTRAINT family_groups_pkey PRIMARY KEY (id),
	CONSTRAINT fkjjvfhlch7k8w82jcmtyjwlr7o FOREIGN KEY (primary_host_user_id) REFERENCES public.users(id)
);
CREATE INDEX idx_family_group_primary_host ON public.family_groups USING btree (primary_host_user_id);


-- public.family_invitations definition

-- Drop table

-- DROP TABLE public.family_invitations;

CREATE TABLE public.family_invitations (
	id uuid NOT NULL,
	accepted_at timestamp(6) NULL,
	accepted_by_user_id uuid NULL,
	created_at timestamp(6) NOT NULL,
	expires_at timestamp(6) NOT NULL,
	family_group_id uuid NOT NULL,
	invited_by_user_id uuid NOT NULL,
	status varchar(20) NOT NULL,
	target_role varchar(30) NOT NULL,
	"token" varchar(20) NOT NULL,
	CONSTRAINT family_invitations_pkey PRIMARY KEY (id),
	CONSTRAINT family_invitations_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'ACCEPTED'::character varying, 'EXPIRED'::character varying, 'CANCELLED'::character varying])::text[]))),
	CONSTRAINT family_invitations_target_role_check CHECK (((target_role)::text = ANY ((ARRAY['PRIMARY_HOST'::character varying, 'SECONDARY_HOST'::character varying, 'PROTECTED'::character varying])::text[]))),
	CONSTRAINT uk14oa9o75ucmuymj5invwal2bo UNIQUE (token),
	CONSTRAINT fkaokm95ewirbumidrloee7bkcr FOREIGN KEY (family_group_id) REFERENCES public.family_groups(id),
	CONSTRAINT fkcuuyq05jdhyhiowqsdt5e0eu7 FOREIGN KEY (invited_by_user_id) REFERENCES public.users(id),
	CONSTRAINT fkiiim2f0jvwjnckmcarpl5dll4 FOREIGN KEY (accepted_by_user_id) REFERENCES public.users(id)
);
CREATE INDEX idx_family_invitation_group ON public.family_invitations USING btree (family_group_id);
CREATE INDEX idx_family_invitation_inviter ON public.family_invitations USING btree (invited_by_user_id);


-- public.invitations definition

-- Drop table

-- DROP TABLE public.invitations;

CREATE TABLE public.invitations (
	id uuid NOT NULL,
	accepted_at timestamp(6) NULL,
	accepted_by_user_id uuid NULL,
	created_at timestamp(6) NOT NULL,
	expires_at timestamp(6) NOT NULL,
	host_id uuid NOT NULL,
	host_name varchar(100) NULL,
	status varchar(20) NOT NULL,
	"token" varchar(20) NOT NULL,
	CONSTRAINT invitations_pkey PRIMARY KEY (id),
	CONSTRAINT invitations_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'ACCEPTED'::character varying, 'EXPIRED'::character varying, 'CANCELLED'::character varying])::text[]))),
	CONSTRAINT ukt4i6esv44p6yi7cxq277vlo3i UNIQUE (token),
	CONSTRAINT fkbk4a8bfo0266d0pcqhprqixk8 FOREIGN KEY (host_id) REFERENCES public.users(id),
	CONSTRAINT fkhbffs1oebaxvyl7an4ybwsbsj FOREIGN KEY (accepted_by_user_id) REFERENCES public.users(id)
);


-- public.family_group_members definition

-- Drop table

-- DROP TABLE public.family_group_members;

CREATE TABLE public.family_group_members (
	id uuid NOT NULL,
	family_group_id uuid NOT NULL,
	joined_at timestamp(6) NOT NULL,
	"role" varchar(30) NOT NULL,
	user_id uuid NOT NULL,
	CONSTRAINT family_group_members_pkey PRIMARY KEY (id),
	CONSTRAINT family_group_members_role_check CHECK (((role)::text = ANY ((ARRAY['PRIMARY_HOST'::character varying, 'SECONDARY_HOST'::character varying, 'PROTECTED'::character varying])::text[]))),
	CONSTRAINT uk_family_group_user UNIQUE (family_group_id, user_id),
	CONSTRAINT fkpwvficnxvxx1d3rly40u79tj FOREIGN KEY (user_id) REFERENCES public.users(id),
	CONSTRAINT fkrkkcj57g69olxue8elbjr7i10 FOREIGN KEY (family_group_id) REFERENCES public.family_groups(id)
);
CREATE INDEX idx_family_member_group ON public.family_group_members USING btree (family_group_id);
CREATE INDEX idx_family_member_user ON public.family_group_members USING btree (user_id);

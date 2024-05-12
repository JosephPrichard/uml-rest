create table if not exists projects
(
	project_id text not null
		constraint projects_pkey
			primary key,
	content_name text not null,
	lang text not null,
	user_id text,
	timestamp text
);

alter table projects owner to postgres;

create table if not exists classes
(
	class_id text not null
		constraint classes_pkey
			primary key,
	content_name text not null,
	access text not null,
	stereotypes text[] not null,
	project_id text not null
		constraint classes_project_id_fkey
			references projects
				on delete cascade,
	x_pos integer not null,
	y_pos integer not null
);

alter table classes owner to postgres;

create index if not exists fki_g
	on classes (project_id);

create table if not exists methods
(
	method_id text not null
		constraint methods_pkey
			primary key,
	content_name text not null,
	return_type text,
	params text not null,
	class_id text not null
		constraint methods_class_id_fkey
			references classes
				on delete cascade,
	project_id text not null
		constraint methods_project_id_fkey
			references projects
				on delete cascade,
	content_order integer not null,
	access text,
	abstract boolean,
	static boolean
);

alter table methods owner to postgres;

create index if not exists fki_methods_class_id_fkey
	on methods (class_id);

create index if not exists fki_methods_project_id_fkey
	on methods (project_id);

create table if not exists fields
(
	field_id text not null
		constraint fields_pkey
			primary key,
	content_name text not null,
	data_type text,
	class_id text not null
		constraint classes_class_id_fkey
			references classes
				on delete cascade,
	content_order integer not null,
	project_id text not null
		constraint classes_project_id_fkey
			references projects
				on delete cascade,
	access text not null,
	static boolean
);

alter table fields owner to postgres;

create index if not exists fki_fields_project_id_fkey
	on fields (project_id);

create index if not exists fki_fields_class_id_fkey
	on fields (class_id);

create table if not exists relationships
(
	relationship_id text not null
		constraint relationships_pkey
			primary key,
	class_id_from text not null
		constraint relationships_class_id_from_fkey
			references classes
				on delete cascade,
	class_id_to text not null
		constraint relationships_class_id_to_fkey
			references classes
				on delete cascade,
	type text not null,
	project_id text
		constraint relationships_project_id_fkey
			references projects
				on delete cascade,
	label text
);

alter table relationships owner to postgres;

create index if not exists fki_relationships_project_id_fkey
	on relationships (project_id);

create index if not exists fki_relationships_class_id_from_fkey
	on relationships (class_id_from);

create index if not exists fki_relationships_class_id_to_fkey
	on relationships (class_id_to);

create table if not exists packages
(
	package_id text not null
		constraint packages_pkey
			primary key,
	x_pos integer not null,
	y_pos integer not null,
	x_dist integer not null,
	y_dist integer not null,
	project_id text not null,
	content_name text not null
);

alter table packages owner to postgres;


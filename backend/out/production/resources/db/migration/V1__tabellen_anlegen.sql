create table test_set
(
    id                           serial primary key,
    start_time                   timestamp not null,
    end_time                     timestamp not null,
    result_publication_timestamp timestamp not null
);

create table freitext_frage
(
    test_set           integer references test_set (id),
    test_set_key       integer,
    id                 uuid primary key,
    fragestellung      varchar(500),
    max_punkte         integer,
    loesungs_vorschlag varchar(500)
);

create table multiple_choice_frage
(
    test_set      integer references test_set (id),
    test_set_key  integer,
    id            uuid primary key,
    fragestellung varchar(500),
    max_punkte    integer,
    erklaerung    varchar(500)
);

create table multiple_choice_option
(
    multiple_choice_frage     uuid references multiple_choice_frage (id),
    multiple_choice_frage_key integer,
    id                        uuid primary key,
    text                      TEXT    not null,
    korrekt                   BOOLEAN not null
);

create table test_set_einreichung
(
    id                       uuid primary key,
    test_set_id              integer references test_set (id),
    user_id                  varchar(200),
    nicht_bestanden_override boolean default false,
    nicht_bestanden_reason   varchar(300),
    version                  integer default null -- für optimistisches Locking
);

create table freitext_einreichung
(
    test_set_einreichung     uuid references test_set_einreichung (id),
    test_set_einreichung_key integer,
    id                       UUID primary key,
    frage_id                 UUID references freitext_frage (id),
    user_id                  varchar(300),
    antwort_text             varchar(500),
    korrektor_id             varchar(255),
    punktzahl                integer,
    feedback                 varchar(500)
);

create table multiple_choice_einreichung
(
    test_set_einreichung     uuid references test_set_einreichung (id),
    test_set_einreichung_key integer,
    id                       uuid primary key,
    user_id                  varchar(300),
    frage_id                 UUID references multiple_choice_frage (id),
    erreichte_punktzahl      integer
);

create table multiple_choice_einreichung_auswahl
(
    multiple_choice_einreichung uuid references multiple_choice_einreichung (id),
    ausgewaehlte_id             UUID references multiple_choice_option (id)
);
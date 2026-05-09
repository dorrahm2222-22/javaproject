-- Ben Ali Mohamed
INSERT INTO
    utilisateur (
        login,
        motdepasse,
        role,
        email,
        actif
    )
VALUES (
        'mohamed.benali',
        '123456',
        'ENSEIGNANT',
        'mohamed@ecole.tn',
        1
    );

INSERT INTO
    enseignant (
        id,
        nom,
        prenom,
        telephone,
        matiere_id
    )
VALUES (
        LAST_INSERT_ID(),
        'Ben Ali',
        'Mohamed',
        '21000001',
        1
    );

-- Chabi Ahmed
INSERT INTO
    utilisateur (
        login,
        motdepasse,
        role,
        email,
        actif
    )
VALUES (
        'ahmed.chabi',
        '123456',
        'ENSEIGNANT',
        'ahmed@ecole.tn',
        1
    );

INSERT INTO
    enseignant (
        id,
        nom,
        prenom,
        telephone,
        matiere_id
    )
VALUES (
        LAST_INSERT_ID(),
        'Chabi',
        'Ahmed',
        '21000002',
        2
    );

-- Bouazizi Sara
INSERT INTO
    utilisateur (
        login,
        motdepasse,
        role,
        email,
        actif
    )
VALUES (
        'sara.bouazizi',
        '123456',
        'ENSEIGNANT',
        'sara@ecole.tn',
        1
    );

INSERT INTO
    enseignant (
        id,
        nom,
        prenom,
        telephone,
        matiere_id
    )
VALUES (
        LAST_INSERT_ID(),
        'Bouazizi',
        'Sara',
        '21000003',
        3
    );

-- Triki Fatma
INSERT INTO
    utilisateur (
        login,
        motdepasse,
        role,
        email,
        actif
    )
VALUES (
        'fatma.triki',
        '123456',
        'ENSEIGNANT',
        'fatma@ecole.tn',
        1
    );

INSERT INTO
    enseignant (
        id,
        nom,
        prenom,
        telephone,
        matiere_id
    )
VALUES (
        LAST_INSERT_ID(),
        'Triki',
        'Fatma',
        '21000004',
        4
    );
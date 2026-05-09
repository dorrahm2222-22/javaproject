-- Adam Ben Salem
INSERT INTO
    utilisateur (
        login,
        motdepasse,
        role,
        email,
        actif
    )
VALUES (
        'adam.bensalem',
        '123456789',
        'ETUDIANT',
        'eya@ecole.com',
        1
    );

INSERT INTO
    etudiant (
        id,
        nom,
        prenom,
        date_naissance,
        niveau
    )
VALUES (
        LAST_INSERT_ID(),
        'Ben Salem',
        'Adam',
        '2008-03-15',
        '6ème'
    );

-- Laila Elgasmi
INSERT INTO
    utilisateur (
        login,
        motdepasse,
        role,
        email,
        actif
    )
VALUES (
        'leila.kasmi',
        '123456789',
        'ETUDIANT',
        'leila@ecole.com',
        1
    );

INSERT INTO
    etudiant (
        id,
        nom,
        prenom,
        date_naissance,
        niveau
    )
VALUES (
        LAST_INSERT_ID(),
        'Elgasmi',
        'Laila',
        '2008-07-22',
        '6ème'
    );

-- Youssef Mansouri
INSERT INTO
    utilisateur (
        login,
        motdepasse,
        role,
        email,
        actif
    )
VALUES (
        'youssef.mansouri',
        '123456',
        'ETUDIANT',
        'youssef@ecole.tn',
        1
    );

INSERT INTO
    etudiant (
        id,
        nom,
        prenom,
        date_naissance,
        niveau
    )
VALUES (
        LAST_INSERT_ID(),
        'Mansouri',
        'Youssef',
        '2007-11-05',
        '7ème'
    );

-- Mariem Zghlami
INSERT INTO
    utilisateur (
        login,
        motdepasse,
        role,
        email,
        actif
    )
VALUES (
        'mariem.zghlami',
        '123456',
        'ETUDIANT',
        'mariem@ecole.tn',
        1
    );

INSERT INTO
    etudiant (
        id,
        nom,
        prenom,
        date_naissance,
        niveau
    )
VALUES (
        LAST_INSERT_ID(),
        'Zghlami',
        'Mariem',
        '2007-04-18',
        '7ème'
    );

-- Anas Ben Yousef
INSERT INTO
    utilisateur (
        login,
        motdepasse,
        role,
        email,
        actif
    )
VALUES (
        'anas.benyoussef',
        '123456',
        'ETUDIANT',
        'anas@ecole.tn',
        1
    );

INSERT INTO
    etudiant (
        id,
        nom,
        prenom,
        date_naissance,
        niveau
    )
VALUES (
        LAST_INSERT_ID(),
        'Ben Yousef',
        'Anas',
        '2006-09-30',
        '8ème'
    );

-- Nour Hammami
INSERT INTO
    utilisateur (
        login,
        motdepasse,
        role,
        email,
        actif
    )
VALUES (
        'nour.hammami',
        '123456',
        'ETUDIANT',
        'nour@ecole.tn',
        1
    );

INSERT INTO
    etudiant (
        id,
        nom,
        prenom,
        date_naissance,
        niveau
    )
VALUES (
        LAST_INSERT_ID(),
        'Hammami',
        'Nour',
        '2006-01-12',
        '8ème'
    );
DELIMITER / /

CREATE TRIGGER after_note_change
AFTER UPDATE ON note
FOR EACH ROW
BEGIN
    -- Calculate new average
    SET @new_avg = (SELECT AVG(note) FROM note WHERE etudiant_id = NEW.etudiant_id);
    
    -- Update the moyenneg table
    UPDATE moyenneg 
    SET valeur = @new_avg,
        mention = CASE 
            WHEN @new_avg >= 16 THEN 'Très Bien'
            WHEN @new_avg >= 14 THEN 'Bien'
            WHEN @new_avg >= 12 THEN 'Assez Bien'
            WHEN @new_avg >= 10 THEN 'Passable'
            ELSE 'Insuffisant'
        END
    WHERE etudiant_id = NEW.etudiant_id;
END

DELIMITER;
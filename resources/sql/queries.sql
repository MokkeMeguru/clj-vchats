-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO USERS
(password, mail)
VALUES (:pass, :mail)

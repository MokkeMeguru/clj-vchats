-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO USERS
(user_name, password, mail)
VALUES (:name, :pass, :mail)

-- :name get-user :? :1
-- :doc get user with user-name
SELECT user_name, password FROM USERS
WHERE user_name = :name

-- :name get-users :? :*
-- :doc get all users
SELECT user_name FROM USERS

-- :name delete-user! :! :1
-- :doc delete-user with user_name
DELETE FROM USERS
WHERE user_name = :name


-- :name create-channel! :! :1
-- :doc create channel with channel_name
INSERT INTO CHANNELS
(chan_name, master_name)
VALUES (:chan_name, :master_name)

-- :name get-channels :? :*
-- :doc get all channels
SELECT chan_name FROM CHANNELS

-- :name get-channel :? :1
-- :doc get one channel by chan_name
SELECT * FROM CHANNELS
WHERE chan_name = :chan_name

-- :name delete-channel! :! :1
-- :doc delete channel by chan_name
DELETE FROM CHANNELS
WHERE chan_name = :chan_name

-- :name save-message! :! :1
-- :doc save message
INSERT INTO MESSAGES
(chan_name, user_name, messages, ltime)
VALUES (:chan_name, :name, :params, :ltime)

-- :name get-messages :? :*
-- :doc get all messages
SELECT chan_name, user_name, messages, ltime FROM MESSAGES
ORDER BY ltime DESC

-- :name get-message :? :*
-- :doc get message with chan_name
SELECT user_name, messages, ltime FROM MESSAGES
WHERE chan_name = :chan_name

-- :name delete-message! :! :1
-- :doc delete messages with chan_name
DELETE FROM MESSAGES
WHERE chan_name = :chan_name

-- :name save-audios! :! :1
-- :doc save audio
INSERT INTO AUDIOS
(id, chan_name, audio)
VALUES (:id, :chan_name, :audio)

-- :name get-inviter :? :1
-- :doc get invite user
SELECT inviter_name FROM CHANNELS
WHERE chan_name = :chan_name

-- :name get-master :? :1
-- :doc get master user
SELECT master_name FROM CHANNELS
WHERE chan_name = :chan_name

-- :name set-inviter! :! :1
-- :doc set inviter
UPDATE CHANNELS
SET inviter_name = :inviter_name
WHERE chan_name = :chan_name

-- :name delete-inviter! :! :1
UPDATE CHANNELS
SET inviter_name = NULL
WHERE chan_name = :chan_name

-- :name set-inviter-face! :! :1
UPDATE CHANNELS
SET inviter_face = :inviter_face
WHERE chan_name = :chan_name

-- :name set-inviter-bg! :! :1
UPDATE CHANNELS
SET inviter_bg = :inviter_bg
WHERE chan_name = :chan_name

-- :name set-master-face! :! :1
UPDATE CHANNELS
SET master_face = :master_face
WHERE chan_name = :chan_name

-- :name set-master-bg! :! :1
UPDATE CHANNELS
SET master_bg = :master_bg
WHERE chan_name = :chan_name

-- :name get-skin :? :1
-- :doc get master user
SELECT master_face, master_bg, inviter_face, inviter_bg FROM CHANNELS
WHERE chan_name = :chan_name

#Для получения всех сообщений
SELECT 
	USER_FROM, 
	MESSAGE, 
	TIME_STAMP,
    STATUS.TITLE AS STATUS
FROM 
	MESSAGE 
JOIN 
	STATUS ON MESSAGE.STATUS = STATUS.ID
ORDER BY TIME_STAMP DESC;

#Для получения n сообщений
SELECT 
	USER_FROM, 
	MESSAGE, 
	TIME_STAMP,
    STATUS.TITLE AS STATUS
FROM 
	MESSAGE 
JOIN 
	STATUS ON MESSAGE.STATUS = STATUS.ID
ORDER BY TIME_STAMP DESC;
LIMIT ?;

#Для получения всех пользователей и их ролей
SELECT 
	NICK, 
    TITLE AS ROLE
FROM 
	USER 
JOIN 
	ROLE ON USER.ROLE = ROLE.ID
ORDER BY NICK;

#Send message
INSERT INTO MESSAGE(USER_FROM, MESSAGE, TIME_STAMP, STATUS)
VALUES('%s', '%s', '2021-05-05 18:49:42', 1);

#Удаление сообщения о кике пользователя
DELETE 
FROM 
	MESSAGE
WHERE 
	STATUS = 3 AND MESSAGE = '%s';
    
                                
#Для создания нового пользователя
INSERT INTO USER(NICK, ROLE)
VALUES('%s', '%s');

#Получение роли пользователя
SELECT 
	TITLE AS ROLE
FROM
	USER
JOIN 
	ROLE ON USER.ROLE = ROLE.ID
WHERE 
	NICK = '%s';
    
#Получения пользователя по нику
SELECT 
	NICK,
	TITLE AS ROLE
FROM
	USER
JOIN 
	ROLE ON USER.ROLE = ROLE.ID
WHERE 
	NICK = '%s';
    
#Для получения кикнутых пользователей
SELECT 
	NICK,
    TITLE AS ROLE
FROM 
	USER
JOIN
	ROLE ON USER.ROLE = ROLE.ID
WHERE
	NICK IN (	SELECT 
					MESSAGE AS NICK
				FROM 
					MESSAGE 
				WHERE 
					STATUS = 3 
			)
ORDER BY 
	NICK;

#Для получаения залогиненых пользователей
SELECT 
	NICK,
    ROLE.TITLE AS ROLE
FROM 
	MESSAGE
JOIN 
	STATUS ON MESSAGE.STATUS = STATUS.ID
JOIN 
	USER ON MESSAGE.USER_FROM = USER.NICK
JOIN 
	ROLE ON USER.ROLE = ROLE.ID
WHERE 
	STATUS = 1 OR STATUS = 4
GROUP BY 
	NICK
HAVING 
	(COUNT(STATUS) % 2) <> 0 
ORDER BY 
	NICK;

#Проверка кикнут ли пользователь
SELECT DISTINCT	EXISTS (SELECT 
							MESSAGE
						FROM 
							MESSAGE
						JOIN 
							STATUS ON MESSAGE.STATUS = STATUS.ID
						JOIN 
							USER ON MESSAGE.USER_FROM = USER.NICK
                        WHERE STATUS = 3 AND MESSAGE = '%s'
						) AS Result
FROM 
	MESSAGE;
	
#Проверка залогинен ли пользователь
SELECT DISTINCT
CASE
    WHEN '%s' NOT IN (	SELECT 
							NICK 
						FROM 
							MESSAGE
						JOIN 
							STATUS ON MESSAGE.STATUS = STATUS.ID
						JOIN 
							USER ON MESSAGE.USER_FROM = USER.NICK
						)
        THEN FALSE
    WHEN 'LOGOUT' = (	SELECT 
							TITLE 
						FROM 
							MESSAGE
						JOIN 
							STATUS ON MESSAGE.STATUS = STATUS.ID
						JOIN 
							USER ON MESSAGE.USER_FROM = USER.NICK
						WHERE 
							nick = '%s'
						ORDER BY 
							TIME_STAMP DESC
						LIMIT 1
					)
        THEN FALSE
    ELSE TRUE
END AS Result
FROM MESSAGE;




import mysql.connector
from mysql.connector import pooling

DB_CONFIG = {
    'host':     'localhost',
    'user':     'root',
    'password': '',
    'database': 'sms_db',
    'port':     3306
}

#Connection pool - avoids opening a new connection on every request
_pool = pooling.MySQLConnectionPool(
    pool_name='sms_pool',
    pool_size=5,
    **DB_CONFIG
)


def get_connection():
    return _pool.get_connection()


def execute_query(sql: str, params: tuple = ()):
    #Execute a SELECT query and return all rows as a list of dicts
    conn = get_connection()
    try:
        cursor = conn.cursor(dictionary=True)
        cursor.execute(sql, params)
        return cursor.fetchall()
    finally:
        cursor.close()
        conn.close()


def execute_update(sql: str, params: tuple = ()):
    #Execute an INSERT, UPDATE or DELETE query
    #Returns True if at least one row was affected, False otherwise
    conn = get_connection()
    try:
        cursor = conn.cursor()
        cursor.execute(sql, params)
        conn.commit()
        return cursor.rowcount > 0
    finally:
        cursor.close()
        conn.close()
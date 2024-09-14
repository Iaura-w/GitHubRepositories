conn = new Mongo();
db = conn.getDB("repositories");

db.createUser(
    {
        user: "admin",
        pwd: "admin",
        roles: [
            {
                role: "readWrite",
                db: "repositories"
            }
        ]
    }
)
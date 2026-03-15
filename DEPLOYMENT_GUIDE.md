# 🚀 Deployment Guide: Render + Neon

This guide will help you host your **Smart Appointment System** for free using PostgreSQL.

## 1. Set up the Database (Neon)
1.  Go to [Neon.tech](https://neon.tech/) and sign up.
2.  Create a new project (call it `appointment-system`).
3.  In the dashboard, you will see a **Connection String**. It looks like this:
    `postgresql://user:pass@ep-cool-flower-123456.neon.tech/neondb?sslmode=require`
4.  Copy this string. **CRITICAL:** Java needs it to start with `jdbc:`.
    *   If Neon gives you `postgresql://...`
    *   You must paste it as `jdbc:postgresql://...`
5.  Also make sure it ends with `?sslmode=require`.
1.  Push your code to a **GitHub repository**.
2.  Go to [Render.com](https://render.com/) and sign up.
3.  Click **New +** > **Web Service**.
4.  Connect your GitHub repository.
5.  **Settings:**
    *   **Name:** `appointment-system`
    *   **Runtime:** `Docker` (Render will automatically find your `Dockerfile`).
    *   **Free Tier:** Select the "Free" instance type.
6.  Click **Advanced** > **Add Environment Variable**:
    | Key | Value |
    | :--- | :--- |
    | `DATABASE_URL` | *Paste your Neon Connection String here* |
    | `EMAIL_USERNAME` | *Your Gmail address* |
    | `EMAIL_PASSWORD` | *Your Gmail App Password* |

## 3. Verify
1.  Once Render finishes building (it takes 3-5 mins), it will give you a URL like `https://appointment-system.onrender.com`.
2.  Open it! Your app should automatically create the database tables in Neon and be ready to use.

---

### 💡 Pro Tip
Render's free tier goes to sleep after 15 minutes of inactivity. The first time you open the link after a break, it might take 30 seconds to load. This is normal for free hosting!

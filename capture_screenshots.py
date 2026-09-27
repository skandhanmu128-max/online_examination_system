import asyncio
from playwright.async_api import async_playwright

async def capture():
    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)
        context = await browser.new_context(viewport={'width': 1280, 'height': 800})
        page = await context.new_page()

        # 1. Login View
        await page.goto("http://localhost:8080")
        await page.wait_for_selector("#loginUsername")
        await page.screenshot(path="screenshot_login.png")
        print("Captured screenshot_login.png")

        # 2. Login as Student & Capture Student Portal
        await page.fill("#loginUsername", "student1")
        await page.fill("#loginPassword", "pass123")
        await page.click("button[type='submit']")
        await page.wait_for_selector("#studentPortalView.active")
        await page.wait_for_timeout(1000)
        await page.screenshot(path="screenshot_student_portal.png")
        print("Captured screenshot_student_portal.png")

        # 3. Start Exam & Capture Exam Workspace
        await page.click(".exam-card button")
        await page.wait_for_selector("#examWorkspaceView.active")
        await page.wait_for_timeout(1000)
        await page.screenshot(path="screenshot_exam_workspace.png")
        print("Captured screenshot_exam_workspace.png")

        # 4. Trigger Anti-Cheat Modal & Capture Alert
        await page.evaluate("document.getElementById('antiCheatModal').classList.add('active')")
        await page.screenshot(path="screenshot_anticheat_alert.png")
        print("Captured screenshot_anticheat_alert.png")
        await page.evaluate("document.getElementById('antiCheatModal').classList.remove('active')")

        # 5. Answer questions and Submit -> Capture Scorecard
        await page.click("button:has-text('Submit Exam')")
        await page.wait_for_timeout(500)
        # Handle confirm prompt if any, or trigger submit directly
        await page.evaluate("app.submitExam(false)")
        await page.wait_for_selector("#resultView.active")
        await page.wait_for_timeout(1000)
        await page.screenshot(path="screenshot_scorecard.png")
        print("Captured screenshot_scorecard.png")

        # 6. Logout & Login as Admin -> Capture Admin Dashboard
        await page.click("button:has-text('Logout')")
        await page.wait_for_selector("#authView.active")
        await page.fill("#loginUsername", "admin")
        await page.fill("#loginPassword", "admin123")
        await page.click("button[type='submit']")
        await page.wait_for_selector("#adminDashboardView.active")
        await page.wait_for_timeout(1500)
        await page.screenshot(path="screenshot_admin_dashboard.png")
        print("Captured screenshot_admin_dashboard.png")

        await browser.close()

if __name__ == "__main__":
    asyncio.run(capture())

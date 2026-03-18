# AGENTS.md

## Cursor Cloud specific instructions

**TapLink** — a static NFC digital visiting card landing page built with Vite, vanilla JS, HTML, and CSS. No backend, no database, no tests.

### Quick reference

| Task | Command |
|------|---------|
| Install deps | `npm install` |
| Dev server | `npm run dev` (port 5173, host 0.0.0.0) |
| Build | `npm run build` |
| Lint JS files | `npx eslint js/ eslint.config.js vite.config.js` |

### Non-obvious caveats

- **Lint script is broken**: `npm run lint` fails because it passes `--ext .js,.html` which causes ESLint v9 (flat config) to try parsing HTML files. Use `npx eslint js/ eslint.config.js vite.config.js` to lint just JS files instead.
- **No test framework**: There are no automated tests in this project.
- **Google Fonts**: Loaded via CDN; the page renders fine without network access but uses system fallback fonts.

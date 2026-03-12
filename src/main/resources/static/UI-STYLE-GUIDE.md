# 🎨 Premium Recruitment & Onboarding System - UI/UX Design System

## Overview

A comprehensive, modern, and premium design system for a full-stack recruitment and onboarding management platform. Built with glassmorphic effects, gradient-based theming, and professional SaaS UI patterns.

---

## 📂 File Structure

```
recruiting-system/src/main/resources/static/
├── css/
│   ├── design-system.css        # Core design tokens & base styles
│   ├── layout.css               # Sidebar, navbar, modal layouts
│   ├── components.css           # Dashboard cards, tables, badges
│
├── applicant-dashboard.html         # 🟢 Applicant Module
├── recruiter-dashboard.html         # 🔵 Recruiter Module
├── hiring-manager-dashboard.html    # 🟣 Hiring Manager Module
├── hr-manager-dashboard.html        # 🟠 HR Manager Module
├── onboarding-dashboard.html        # 🟡 Onboarding Team Module
├── admin-dashboard.html             # 🔴 Admin Module
└── UI-STYLE-GUIDE.md               # This file
```

---

## 🎨 Design System

### Color Palette

```
Primary Gradient: #667eea → #764ba2 (Purple-Blue)
Accent Colors:
  - Success: #10b981 (Green)
  - Warning: #f59e0b (Amber)
  - Danger: #ef4444 (Red)
  - Info: #3b82f6 (Blue)

Grayscale (Light Mode):
  - Primary BG: #ffffff
  - Secondary BG: #f9fafb
  - Text Primary: #111827
  - Text Secondary: #6b7280
  - Borders: #e5e7eb
```

### Typography

```
Font Family: System fonts (-apple-system, Segoe UI, Roboto)

Sizes:
  - H1: 36px (font-weight: 700)
  - H2: 30px (font-weight: 700)
  - H3: 24px (font-weight: 700)
  - Body: 16px (font-weight: 400)
  - Small: 14px
  - Tiny: 12px
```

### Spacing

```
xs: 4px    md: 16px    2xl: 48px
sm: 8px    lg: 24px    3xl: 64px
          xl: 32px
```

### Border Radius

```
sm: 6px    lg: 12px    full: 9999px
md: 8px    xl: 16px
          2xl: 24px
```

### Shadows

```
Glass Shadow (SM): 0 8px 32px rgba(31, 38, 135, 0.15)
Glass Shadow (LG): 0 8px 32px rgba(31, 38, 135, 0.37)
Shadow (MD): 0 4px 6px rgba(0, 0, 0, 0.1)
Shadow (LG): 0 10px 15px rgba(0, 0, 0, 0.1)
```

### Transitions

```
Fast: 150ms (cubic-bezier(0.4, 0, 0.2, 1))
Base: 200ms (cubic-bezier(0.4, 0, 0.2, 1))
Slow: 300ms (cubic-bezier(0.4, 0, 0.2, 1))
```

---

## 🎯 Module Overview

### 🟢 **APPLICANT DASHBOARD**

**Purpose:** Job seeker's portal for managing applications and interviews

**Key Features:**
- ✓ Welcome banner with personalized greeting
- ✓ Quick stats: Total Applications, Active Interviews, Offers, Profile Score
- ✓ Active applications timeline with status tracking
- ✓ Interview countdown cards with details
- ✓ Recommended job opportunities with match percentage
- ✓ Notifications with interview reminders

**Key Components:**
- `metric-card` - KPI display with icons and trends
- `application-status-card` - Application progress with timeline
- `interview-countdown` - Upcoming interview alert
- `job-card` - Job opportunity recommendation

**Color Theme:**
- Primary: Purple-Blue gradient
- Accents: Warning (orange) for interviews, Success (green) for offers
- Vibe: Friendly, approachable, motivational

---

### 🔵 **RECRUITER DASHBOARD**

**Purpose:** Recruitment workflow management and candidate tracking

**Key Features:**
- ✓ KPI cards: Total Applicants, Shortlisted, In Interviews, Offers Extended
- ✓ Upcoming interviews panel with quick actions
- ✓ Kanban pipeline: New → Shortlisted → Interview → Offers
- ✓ Candidate mini-cards with drag-drop capability
- ✓ Filter bar for position, status
- ✓ Interview scheduling and feedback collection

**Key Components:**
- `pipeline-column` - Kanban columns
- `candidate-item` - Draggable candidate cards
- `status-indicator` - Color-coded status dots
- `filter-bar` - Advanced filtering

**Color Theme:**
- Primary: Purple (candidate stages)
- Status Colors: New (blue), Shortlisted (purple), Interview (amber), Offers (green)
- Vibe: Professional, workflow-focused, data-driven

---

### 🟣 **HIRING MANAGER DASHBOARD**

**Purpose:** Candidate evaluation and hiring decision portal

**Key Features:**
- ✓ Pending reviews, Completed reviews, Interview stats
- ✓ Split-screen evaluation: Resume viewer + Rating panel
- ✓ Multi-factor evaluation sliders (Technical, Experience, Communication, Culture)
- ✓ One-click decision buttons: Approve, Hold, Reject
- ✓ Interview history timeline
- ✓ Forwarded candidates table with status

**Key Components:**
- `resume-viewer` - Document display area
- `evaluation-panel` - Sliders, ratings, feedback
- `rating-slider` - Custom range input
- `interview-history` - Timeline of interactions

**Color Theme:**
- Evaluation: Gradient sliders
- Actions: Green (Approve), Amber (Hold), Red (Reject)
- Vibe: Decision-focused, clean evaluation interface

---

### 🟠 **HR MANAGER DASHBOARD**

**Purpose:** Offer management, job postings, background checks, employee data

**Key Features:**
- ✓ KPI cards: Offers Created, Pending, Accepted, Active Employees
- ✓ Tabbed interface: Offers, Jobs, Background Checks
- ✓ Offer cards with status badges and salary display
- ✓ Job positions table with status (Open/Closed/On Hold)
- ✓ Background check tracking table
- ✓ Document preview and verification

**Key Components:**
- `offer-card` - Offer details with action buttons
- `tabs-container` - Tab navigation interface
- `collapsible-section` - Expandable sections
- `role-badge` - Position type indicators

**Color Theme:**
- Status: Success (Accepted), Warning (Pending), Primary (Draft), Danger (Declined)
- Vibe: Corporate, organized, policy-focused

---

### 🟡 **ONBOARDING TEAM DASHBOARD**

**Purpose:** Employee onboarding process and document verification

**Key Features:**
- ✓ KPI cards: Total Onboarding, In Progress, Completed, Pending Verification
- ✓ Document verification grid with file types
- ✓ Onboarding progress bars for each employee
- ✓ Interactive checklist: Documentation, Workstation, Training
- ✓ Document cards with action buttons (Review, Reject, Verify)
- ✓ Progress tracking with timeline

**Key Components:**
- `document-card` - Uploaded document cards
- `onboarding-employee` - Employee progress row
- `checklist-item` - Interactive checkbox items
- `progress` - Circular/linear progress bars

**Color Theme:**
- Status: Success (Verified), Warning (Pending), Danger (Rejected)
- Accents: ID (blue), Documents (green), Workstation (orange)
- Vibe: Process-oriented, verification-focused, thorough

---

### 🔴 **ADMIN DASHBOARD**

**Purpose:** System administration, user management, roles, configuration

**Key Features:**
- ✓ System statistics: Users, Sessions, Storage, Uptime, Alerts
- ✓ User management table with role assignment
- ✓ Role & permissions configuration with toggles
- ✓ Collapsible configuration sections
- ✓ Permission matrix for each role
- ✓ Master data and system settings

**Key Components:**
- `admin-stat-card` - System metrics
- `collapsible-section` - Expandable admin panels
- `role-badge` - Role indicators (Recruiter, HR, Admin, etc.)
- `permission-toggle` - Enable/disable permissions
- `config-setting` - Configuration key-value pairs

**Color Theme:**
- Roles: Each role has distinct badge color
- Status: Green (Active), Red (Inactive)
- Vibe: Powerful, complete control, technical

---

## 🏗️ Core Components

### Metric Card
Display KPIs with icon, value, and trend

```html
<div class="metric-card">
  <div class="metric-header">
    <span class="metric-title">Total Applicants</span>
    <div class="metric-icon primary">
      <i class="fas fa-users"></i>
    </div>
  </div>
  <div class="metric-value">342</div>
  <div class="metric-change positive">
    <i class="fas fa-arrow-up"></i>
    <span>+28 this month</span>
  </div>
</div>
```

### Badge
Status indicators and labels

```html
<span class="badge badge-success">Verified</span>
<span class="badge badge-warning">Pending</span>
<span class="badge badge-danger">Rejected</span>
```

### Button Variants
```html
<button class="btn btn-primary">Primary</button>
<button class="btn btn-secondary">Secondary</button>
<button class="btn btn-danger">Danger</button>
<button class="btn btn-ghost">Ghost</button>
```

### Table
Responsive data display with zebra striping and hover effects

```html
<table class="table">
  <thead>
    <tr>
      <th>Header 1</th>
      <th>Header 2</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Data 1</td>
      <td>Data 2</td>
    </tr>
  </tbody>
</table>
```

### Sidebar
Fixed navigation with collapsible sections and user info

```html
<aside class="sidebar">
  <div class="sidebar-header">
    <div class="sidebar-logo">🎯 Recruit</div>
  </div>
  <div class="sidebar-content">
    <!-- Menu items -->
  </div>
  <div class="sidebar-footer">
    <!-- User info -->
  </div>
</aside>
```

### Navbar
Sticky top navigation with search and profile dropdown

```html
<nav class="navbar">
  <div class="navbar-left">
    <!-- Search and title -->
  </div>
  <div class="navbar-right">
    <!-- Notifications and profile -->
  </div>
</nav>
```

---

## 📱 Responsive Design

### Breakpoints
```
sm: 640px   (Tablets)
md: 768px   (Tablets/Laptops)
lg: 1024px  (Desktops)
xl: 1280px  (Wide desktops)
```

### Mobile Behavior
- Sidebar converts to mobile hamburger menu
- Cards stack in single column on mobile
- Tables become horizontally scrollable
- Two-column layouts collapse to single column

---

## ✨ Interaction Patterns

### Hover States
- Cards: Translate up 4px, enhanced shadow
- Buttons: Translate up 2px, enhanced shadow
- Links: Color change to accent
- Sidebar items: Background highlight, border accent

### Focus States
- Inputs: Border color change, glow effect
- Buttons: Visible focus ring
- Links: Underline or color change

### Loading States
- Shimmer animation for placeholders
- Pulse animation for pending items
- Skeleton screens for components

### Animations
- Fade-in: Opacity 0→1, Y translate 10px→0
- Slide-in: X translate -20px→0, opacity fade
- Pulse: 2s opacity oscillation
- Transitions: 150-300ms depending on interaction

---

## 🎬 Usage Examples

### Creating a Dashboard Page

```html
<!DOCTYPE html>
<html>
<head>
  <link rel="stylesheet" href="/css/design-system.css">
  <link rel="stylesheet" href="/css/layout.css">
  <link rel="stylesheet" href="/css/components.css">
</head>
<body>
  <!-- SIDEBAR -->
  <aside class="sidebar">
    <!-- Menu items -->
  </aside>
  
  <!-- MAIN WRAPPER -->
  <div class="main-wrapper">
    <!-- NAVBAR -->
    <nav class="navbar">
      <!-- Search, notifications, profile -->
    </nav>
    
    <!-- MAIN CONTENT -->
    <div class="main-content">
      <div class="content-header">
        <h1 class="content-title">Page Title</h1>
        <p class="content-subtitle">Subtitle</p>
      </div>
      
      <!-- Dashboard grid -->
      <div class="dashboard-grid">
        <div class="metric-card"><!-- Content --></div>
      </div>
    </div>
  </div>
</body>
</html>
```

---

## 🎨 Theme Customization

### Dark Mode
Add `data-theme="dark"` to `<html>` tag

```html
<html data-theme="dark">
```

CSS variables automatically update:
- Background colors inverse
- Text colors inverse
- Borders adapted

### Color Themes
Modify CSS variables in `design-system.css`:

```css
:root {
  --primary-purple: #667eea;
  --primary-blue: #764ba2;
  /* ... other colors ... */
}
```

---

## 📊 Design Principles

1. **Glassmorphism** - Frosted glass effects with blur
2. **Gradient-based** - Purple-blue primary gradient throughout
3. **Responsive** - Mobile-first, works on all devices
4. **Professional** - Clean, corporate aesthetic
5. **Accessible** - WCAG AA compliant colors and contrast
6. **Consistent** - Unified spacing, typography, components
7. **Delightful** - Smooth animations and micro-interactions
8. **Role-specific** - Each role has tailored UX

---

## 🚀 Best Practices

### Typography
- ✓ Use semantic HTML (h1, h2, p)
- ✓ Maintain 1.5-1.6 line height for body text
- ✓ Limit line length to 60-80 characters
- ✓ Use weight hierarchy (300, 400, 600, 700)

### Layout
- ✓ Use CSS Grid for layouts
- ✓ Maintain consistent spacing with spacing scale
- ✓ Follow 8px baseline grid
- ✓ Use card-based layouts for content chunks

### Colors
- ✓ Use semantic color names (success, danger, warning)
- ✓ Maintain 4.5:1 contrast ratio for accessibility
- ✓ Don't rely on color alone for information
- ✓ Use gradients for primary actions

### Components
- ✓ Keep components simple and focused
- ✓ Use BEM naming convention
- ✓ Abstract common patterns
- ✓ Provide variants (primary, secondary, danger)

### Performance
- ✓ Minimize CSS file sizes
- ✓ Use CSS variables for theming
- ✓ Optimize images and icons
- ✓ Lazy load heavy components

---

## 📚 Component Library

All components are fully self-contained in the CSS files:

```
design-system.css (15KB)
  ├── Colors & variables
  ├── Typography
  ├── Buttons
  ├── Forms
  ├── Badges
  ├── Alerts
  └── Utilities

layout.css (10KB)
  ├── Sidebar
  ├── Navbar
  ├── Modals
  └── Dropdowns

components.css (12KB)
  ├── Metric cards
  ├── Tables
  ├── Progress
  ├── Candidate cards
  └── Job cards
```

**Total CSS: ~37KB (before gzip: ~10KB)**

---

## 🎯 Next Steps

### To implement these dashboards:

1. **Copy all CSS files** to `static/css/`
2. **Copy all HTML files** to `static/`
3. **Update your Thymeleaf templates** to use the new design
4. **Integrate with Spring controllers** for data binding
5. **Test responsive design** on mobile devices
6. **Customize colors** as needed for branding

### To extend:

- Add charts using Chart.js or D3.js
- Implement dark mode toggle
- Add more role-specific features
- Create reusable Thymeleaf components
- Build component storybook
- Add accessibility features (ARIA labels)

---

## 📞 Support

For design questions or customizations, refer to:
- CSS variables in `design-system.css`
- Component examples in each dashboard HTML
- Responsive breakpoints in layout.css
- Animation definitions in design-system.css

---

**Last Updated:** December 9, 2025  
**Version:** 1.0  
**Compatibility:** All modern browsers (Chrome, Firefox, Safari, Edge)

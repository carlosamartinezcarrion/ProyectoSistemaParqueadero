---
name: ProPark Systems
colors:
  surface: '#f8f9ff'
  surface-dim: '#ccdbf2'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eef4ff'
  surface-container: '#e5efff'
  surface-container-high: '#dbe9ff'
  surface-container-highest: '#d4e4fa'
  on-surface: '#0d1c2d'
  on-surface-variant: '#434655'
  inverse-surface: '#233143'
  inverse-on-surface: '#e9f1ff'
  outline: '#737686'
  outline-variant: '#c3c6d7'
  surface-tint: '#0053db'
  primary: '#004ac6'
  on-primary: '#ffffff'
  primary-container: '#2563eb'
  on-primary-container: '#eeefff'
  inverse-primary: '#b4c5ff'
  secondary: '#006e2f'
  on-secondary: '#ffffff'
  secondary-container: '#6bff8f'
  on-secondary-container: '#007432'
  tertiary: '#ab0b1c'
  on-tertiary: '#ffffff'
  tertiary-container: '#cf2c30'
  on-tertiary-container: '#ffecea'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dbe1ff'
  primary-fixed-dim: '#b4c5ff'
  on-primary-fixed: '#00174b'
  on-primary-fixed-variant: '#003ea8'
  secondary-fixed: '#6bff8f'
  secondary-fixed-dim: '#4ae176'
  on-secondary-fixed: '#002109'
  on-secondary-fixed-variant: '#005321'
  tertiary-fixed: '#ffdad7'
  tertiary-fixed-dim: '#ffb3ad'
  on-tertiary-fixed: '#410004'
  on-tertiary-fixed-variant: '#930013'
  background: '#f8f9ff'
  on-background: '#0d1c2d'
  surface-variant: '#d4e4fa'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 38px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
  stats-number:
    fontFamily: Inter
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  sidebar-width: 260px
  container-padding: 2rem
  gutter: 1.5rem
  stack-sm: 0.5rem
  stack-md: 1rem
  stack-lg: 1.5rem
---

## Brand & Style

The design system is rooted in **Modern Minimalism**, prioritizing utility, speed of recognition, and administrative efficiency. The brand personality is professional and trustworthy, designed to instill confidence in operators managing high-stakes physical assets. 

The aesthetic leverages a "Software as a Service" (SaaS) visual language: expansive whitespace, a restrained color palette, and high-quality functional typography. The goal is to reduce cognitive load by presenting complex spatial data (parking grids) and numerical metrics through a clean, systematic interface that feels both precise and approachable.

## Colors

The color strategy uses functional signaling to communicate the status of the parking facility at a glance.

- **Primary (Blue):** Used for primary actions, navigation states, and active selections. It represents the "system" and its authority.
- **Success (Green):** Specifically reserved for "Available" spots and positive growth metrics.
- **Danger (Red):** Used for "Occupied" spots, overdue stays, or system alerts.
- **Muted (Gray):** Indicates "Disabled" or "Out of Service" spots and secondary metadata.
- **Background/Surface:** A tiered white-on-gray approach creates clear containment for data-heavy views without the need for heavy borders.

## Typography

The design system utilizes **Inter** for its exceptional legibility in data-heavy environments. 

The hierarchy is built to emphasize "State" and "Quantity." Headlines are tight and bold to define section boundaries, while labels use a slightly increased letter-spacing and uppercase styling to differentiate them from interactive body text. Large "Stats-number" styles are provided for high-level dashboard metrics to ensure critical numbers like "Total Occupancy" are visible from a distance.

## Layout & Spacing

This design system employs a **Fixed Sidebar + Fluid Content** model. 

- **Sidebar:** A 260px fixed-width left navigation ensures system-wide tools are always accessible.
- **Grid:** A 12-column responsive grid is used for dashboard widgets. Widgets typically span 3 columns (quarter-width), 4 columns (third-width), or 6 columns (half-width).
- **Rhythm:** An 8px linear scale (4, 8, 16, 24, 32, 48, 64) governs all padding and margins to maintain a strict visual cadence.
- **Mobile:** On small screens, the sidebar collapses into a bottom navigation bar or a hamburger menu, and grid columns stack vertically (12-column span).

## Elevation & Depth

The design system uses **Tonal Layering** combined with **Ambient Shadows** to create a structured hierarchy.

- **Level 0 (Background):** `#f8fafc` — The canvas on which the application sits.
- **Level 1 (Cards/Sidebar):** White surface with a very soft, diffused shadow (`0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)`). This level is used for the primary content containers and navigation.
- **Level 2 (Modals/Popovers):** Higher elevation with a deeper shadow to pull the element forward from the data grid.

Borders are kept minimal, using a 1px solid stroke in `#e2e8f0` only when necessary to separate table rows or input fields.

## Shapes

The shape language is consistently **Rounded**, using a 12px (0.75rem) base radius for standard components and 16px (1rem) for primary dashboard cards. This creates an interface that feels modern and safe, softening the "industrial" nature of parking management data.

- **Buttons & Inputs:** 12px radius.
- **Dashboard Cards:** 16px radius.
- **Status Indicators (Spots):** 8px radius or fully rounded for "pill" status tags.

## Components

### Buttons
- **Primary:** Solid `#2563eb` with white text. 12px rounded corners. Use for "Add Reservation" or "Export Data."
- **Ghost:** Transparent background with `#2563eb` text. Use for secondary actions like "Cancel."

### Dashboard Cards
White backgrounds with 16px border-radius and Level 1 shadows. Cards should have a consistent header style with `label-md` for the title and `stats-number` for the primary metric.

### Data Tables
- **Header:** Light gray background (`#f1f5f9`) with `label-md` typography.
- **Rows:** White background with a 1px bottom border (`#e2e8f0`). No vertical lines.
- **Padding:** High vertical padding (16px) to ensure touch-friendly targets and readability.

### Parking Grid
The interactive map uses 12px rounded squares to represent parking spots.
- **Available:** Green border with 10% green tint fill.
- **Occupied:** Red border with 10% red tint fill.
- **Hover State:** Increase shadow and add a 2px Primary Blue border to indicate selection.

### Sidebar Navigation
Fixed to the left. Active states use a subtle blue tint background (`#eff6ff`) and a 4px vertical blue stripe on the far left of the menu item. Use Lucide icons at 20px size.
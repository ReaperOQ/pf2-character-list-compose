Pathfinder 2e Remaster: Narrative Flow

Product Overview

The Pitch: A streamlined, card-based character builder for Pathfinder 2e Remaster that removes spreadsheet fatigue. It transforms complex rule interactions into a fluid, visual narrative, culminating in a beautiful, print-optimized character sheet.

For: TTRPG players transitioning from 5e or new to PF2e who want to build characters without getting bogged down by mechanics and math.

Device: desktop

Design Direction: Modern fantasy ledger. Clean card-based layouts, tactile soft shadows, expressive serif headers paired with highly legible modern body text, using exact PF2e action iconography (◆, ◆◆, ◆◆◆, ↺).

Inspired by: longstoryshort.app, Notion, physical tarot decks.



Screens





1. Dashboard: Recent characters and "Start Journey" entry point.



2. Ancestry & Heritage: Card grid selecting biological origins and inherited traits.



3. Background: Searchable, tag-based list of prior life experiences.



4. Class Selection: Heroic focal point defining the character's core mechanics and key attribute.



5. Attribute Matrix: Visual calculation of ability boosts and proficiency math.



6. Print Sheet Generator: Modular, drag-and-drop layout builder for the final physical export.



Key Flows

The ABC Creation Flow: Define character origins mechanically and narratively.





User is on Dashboard -> sees 3 recent character cards and a primary "Forge a Hero" CTA.



User clicks "Forge a Hero" -> advances to Ancestry & Heritage.



User selects "Goblin" card -> reveals slide-out panel with Heritage options (e.g., "Razortooth").



User clicks "Confirm & Continue" -> advances sequentially through Background and Class Selection, building the character data payload.



Design System

Color Palette





Primary: #912020 - Buttons, active tabs, action icons (Remaster Crimson)



Background: #F5F3ED - Page background (Vellum)



Surface: #FFFFFF - Cards, modals, floating panels (Pure White)



Text: #1A1D24 - Body text, primary headings (Deep Ink)



Muted: #6B7280 - Secondary text, helper copy (Slate)



Accent: #C28F27 - Success, level-up indicators, special traits (Brass)



Border: #E5E2D9 - Card outlines, dividers (Dust)

Typography





Headings: Eczar, 700, 24-48px



Body: Albert Sans, 400, 16px



Small text: Albert Sans, 500, 13px (uppercase tracking for tags)



Buttons: Albert Sans, 600, 14px

Style notes: 8px border radius on all cards, box-shadow: 0 4px 20px rgba(26, 29, 36, 0.06), sharp dividers, high contrast for mechanics (e.g., exact action symbols in Crimson).

Design Tokens

:root {
--color-primary: #912020;
--color-background: #F5F3ED;
--color-surface: #FFFFFF;
--color-text: #1A1D24;
--color-muted: #6B7280;
--color-accent: #C28F27;
--color-border: #E5E2D9;

--font-heading: 'Eczar', serif;
--font-body: 'Albert Sans', sans-serif;

--radius-sm: 4px;
--radius-md: 8px;
--radius-lg: 12px;

--shadow-card: 0 4px 20px rgba(26, 29, 36, 0.06);
--shadow-float: 0 12px 32px rgba(26, 29, 36, 0.12);
}





Screen Specifications

Dashboard

Purpose: Entry point and character management.

Layout: 1200px max-width container. 120px top hero section. 3-column masonry grid below.

Key Elements:





Hero Title: 48px Eczar, #1A1D24, "Your Heroes".



Primary CTA: 240px wide, #912020 background, #FFFFFF text, "Forge a Hero" + plus icon.



Character Cards: 360px wide, #FFFFFF surface, 8px radius. Displays Level, Name, Ancestry/Class, and a 64px circular portrait placeholder.

States:





Empty: Large illustrative watermark, single pulsing "Forge a Hero" button in center.



Loading: Skeleton cards with shimmer gradient (#E5E2D9 to #F5F3ED).

Interactions:





Hover Character Card: Y-axis translate -4px, shadow increases to --shadow-float.



Click "Forge a Hero": 300ms fade to Ancestry screen.

Ancestry & Heritage

Purpose: Step 1 of ABC. Select biological traits and hit points.

Layout: Split screen. Left: 800px grid of Ancestry cards. Right: 400px sticky detail panel.

Key Elements:





Ancestry Card: 240px width, 1px #E5E2D9 border. Large Eczar title ("Dwarf"), trait pills (e.g., [Humanoid], [Dwarf]), base HP + Attribute boost preview.



Detail Panel: #FFFFFF background, full height. Shows lore, mechanics, and Heritage dropdown.



Heritage Selector: Custom select input, #F5F3ED background on hover.

Components:





Trait Pill: 24px height, #912020 text, 10% opacity #912020 background, 4px radius.

Interactions:





Click Ancestry Card: Populates Right Panel, slides in from right (300ms cubic-bezier). Card gains 2px #912020 border.

Background

Purpose: Step 2 of ABC. Skill training and attribute boosts.

Layout: Single 800px center column. Sticky bottom action bar.

Key Elements:





Search Bar: 64px height, 24px search icon, "Search 100+ Backgrounds...".



Background Row: Accordion style. Left: Title + Attribute Boosts (e.g., STR or WIS, Free). Right: Granted Skill.



Action Bar: Fixed bottom, #FFFFFF surface, top shadow. "Back" (left), "Continue to Class" (right, #912020).

Interactions:





Click Row: Expands smoothly to show lore text and specific Skill Feat details.



Select Boost: Inline radio group appears for variable boosts ("Choose STR or WIS").

Class Selection

Purpose: Step 3 of ABC. Core mechanics and key attribute.

Layout: Horizontal carousel of large (400x600px) Class "Tarot" Cards.

Key Elements:





Class Card: Full bleed illustration overlay, linear-gradient bottom fade to #1A1D24. Title (Eczar, 32px), Key Attribute (e.g., "Key: Charisma").



Mechanics Preview: Bulleted list of initial proficiencies (Perception, Saves, Attacks).



Subclass Dropdown: Conditional (e.g., Bard Muse, Rogue Racket).

Interactions:





Hover Card: Slight zoom (1.05x) on background illustration.



Click Card: Locks selection, reveals specific subclass choices below the carousel.

Attribute Matrix

Purpose: Final mathematical summary before sheet generation.

Layout: 1000px container. 6-column grid for STR, DEX, CON, INT, WIS, CHA.

Key Elements:





Attribute Column: 120px wide. Header (e.g., "STR"), calculated total (e.g., "+4").



Source Breakdown: Stacked list below total. Shows +1 Ancestry, +1 Background, +1 Class, +1 Free.



Proficiency Legend: U, T, E, M, L visual key in top right. Trained = #C28F27.

Interactions:





Hover Source: Highlights the originating step, tooltip explains rule source.



Click Generate Sheet: Triggers confetti animation, advances to Print Sheet.

Print Sheet Generator

Purpose: Modular assembly for physical print output.

Layout: 2-column. Left: Draggable widget palette. Right: 8.5x11" A4 ratio white canvas area.

Key Elements:





Canvas: #FFFFFF, exact print dimensions, 1px #E5E2D9 border.



Widgets: "Core Stats", "Actions & Reactions", "Strikes", "Spells".



Action Icons: Hardcoded SVG glyphs (◆, ◆◆, ◆◆◆, ↺) scaled perfectly to text height.

Interactions:





Drag & Drop: Snap to grid on canvas. Ghost image while dragging.



Click Print: Triggers browser print dialog, injects @media print CSS hiding all UI except the canvas.





Build Guide

Stack: HTML + Tailwind CSS v3, React/Next.js (for state management)

Build Order:





Design Tokens & Typography: Set up tailwind.config.js with exact Eczar/Albert Sans fonts and custom hex palette.



Dashboard: Establish card base components, shadows, and grid layouts.



Ancestry -> Background -> Class Flow: Build the state machine payload. Critical to implement the slide-out panels and accordion list components here.



Attribute Matrix: Implement the PF2e Remaster calculation engine.



Print Sheet: Build the drag-and-drop grid and specific @media print CSS overrides
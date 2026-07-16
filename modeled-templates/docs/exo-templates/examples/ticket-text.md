# Example - Ticket Text

Resource:

```text
modeled-templates-test/res/docs/tickets.txt.mt
```

Test:

```text
ModeledTemplateDocumentationExamplesTest.rendersTicketTextExampleWithNumberAndDateDefaults
```

## What it demonstrates

- list iteration over ticket tiers;
- date formatting;
- number formatting;
- boolean `if`/`else`;
- default locale and zone supplied by the factory.

## 1. Data

The rendered data is `Conference.ticketTiers`:

```text
Community
  price: 99.0
  includesWorkshop: false
Professional
  price: 249.0
  includesWorkshop: true
```

## 2. Template code

<pre style="background:#111827;color:#e5e7eb;padding:1rem;border-radius:8px;overflow:auto;"><code>Tickets for <span style="color:#34d399">${input.name}</span>
<span style="color:#6ee7b7">#{Date formatting uses modeled defaults for locale/zone unless overridden}</span>
Published: <span style="color:#34d399">${input.publishedAt | (format-date "yyyy-MM-dd")}</span>

<span style="color:#facc15">%(for-each</span> <span style="color:#93c5fd">input.ticketTiers</span> <span style="color:#93c5fd">tier</span><span style="color:#facc15">)</span>
<span style="color:#6ee7b7">#{tier is typed as TicketTier inside the loop}</span>
- <span style="color:#34d399">${tier.name}</span>: <span style="color:#34d399">${tier.price | (format-number "0.00")}</span> EUR
  Available until <span style="color:#34d399">${tier.availableUntil | (format-date "yyyy-MM-dd")}</span>
  <span style="color:#facc15">%(if</span> <span style="color:#93c5fd">tier.includesWorkshop</span><span style="color:#facc15">)</span>
  includes workshops
  <span style="color:#facc15">%(else)</span>
  conference talks only
  <span style="color:#facc15">%(end)</span>
<span style="color:#facc15">%(end)</span></code></pre>

## 3. Exact generated text

```text
Tickets for Hiconic Model Templates Summit
Published: 2026-07-15

- Community: 99.00 EUR
  conference talks only

- Professional: 249.00 EUR
  includes workshops
```

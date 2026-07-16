Tickets for ${input.name}
#{Date formatting uses modeled defaults for locale/zone unless overridden}
Published: ${input.publishedAt | (format-date "yyyy-MM-dd")}

%(for-each input.ticketTiers tier)
#{tier is typed as TicketTier inside the loop}
- ${tier.name}: ${tier.price | (format-number "0.00")} EUR
  Available until ${tier.availableUntil | (format-date "yyyy-MM-dd")}
  %(if tier.includesWorkshop)
  includes workshops
  %(else)
  conference talks only
  %(end)
%(end)

package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import androidx.compose.ui.graphics.Color

// ─── Colours (type-specific) ──────────────────────────────────────────────────
private val HydroBlue  = Color(0xFF0284C7)
private val SolarAmber = Color(0xFFF59E0B)
private val WindBlue   = Color(0xFF3B82F6)
private val BioGreen   = Color(0xFF16A34A)

// ─── Energy type enum ─────────────────────────────────────────────────────────
enum class EnergyType(val label: String, val emoji: String, val color: Color) {

        ALL    ("All",     "🌏", Color(0xFF16A34A)),
        HYDRO  ("Hydro",   "💧", Color(0xFF0284C7)),
        SOLAR  ("Solar",   "☀️", Color(0xFFF59E0B)),
        WIND   ("Wind",    "💨", Color(0xFF3B82F6)),
        BIOMASS("Biomass", "🌿", Color(0xFF16A34A))

}

// ─── Site data class ──────────────────────────────────────────────────────────
data class MalaysianEnergySite(
    val id          : Int,
    val name        : String,
    val state       : String,
    val type        : EnergyType,
    val lat         : Double,
    val lon         : Double,
    val capacity    : String,
    val commissioned: String,
    val imageUrl    : String,
    val shortDesc   : String,
    val fullDesc    : String,
    val function    : String,
    val benefits    : List<String>,
    val sdg7Link    : String,
    val facts       : List<String>
)

// ─── Drawable name map ────────────────────────────────────────────────────────
val SITE_IMAGE_DRAWABLES: Map<Int, String> = mapOf(
    1  to "site_bakun_dam",
    2  to "site_murum_dam",
    3  to "site_pergau_dam",
    4  to "site_temenggor_dam",
    5  to "site_chenderoh",
    6  to "site_lss_sepang",
    7  to "site_pajam_solar",
    8  to "site_lss_bukit_selambau",
    9  to "site_rimba_terjun",
    10 to "site_suria_sungai_petani",
    11 to "site_teluk_intan",
    12 to "site_seguntor",
    13 to "site_tsh_bioenergy",
    14 to "site_jana_landfill",
    15 to "site_kudat_wind",
    16 to "site_mersing_offshore",
    17 to "site_layang_layang",
    18 to "site_ulu_jelai"
)

// ─── Master site list ─────────────────────────────────────────────────────────
val MALAYSIA_ENERGY_SITES: List<MalaysianEnergySite> = listOf(
    MalaysianEnergySite(
        1, "Bakun Dam", "Sarawak", EnergyType.HYDRO, 2.7500, 114.0500,
        "2,400 MW", "2011", "Bakun Dam Sarawak aerial",
        "Malaysia's largest hydroelectric dam, tallest in Southeast Asia.",
        "Bakun Dam is a concrete-faced rockfill dam on the Balui River in Sarawak. Standing 205 m tall with a reservoir of 695 km², it is Malaysia's largest renewable energy facility. Fully commissioned in 2014, it powers the Sarawak Corridor of Renewable Energy (SCORE) initiative and achieved Silver Certification under the Hydropower Sustainability Standard in 2025.",
        "Water from the 695 km² reservoir flows through penstocks to spin 8 Francis-type turbines at the powerhouse. Each turbine drives a generator at 15.75 kV, stepped up to 500 kV for transmission via Sarawak's grid. The large reservoir enables peak-load dispatch — power can be ramped up instantly to meet grid demand.",
        listOf(
            "Provides ~60% of Sarawak's total electricity",
            "Zero direct CO₂ emissions during operation",
            "Enables Sarawak to export surplus power",
            "Supports SCORE heavy industries with clean energy",
            "Silver Certified under Hydropower Sustainability Standard 2025"
        ),
        "Bakun contributes to SDG 7.2 (increase renewable energy share) by supplying large-scale clean electricity to Sarawak and enabling industrial development without fossil fuels.",
        listOf(
            "Height: 205 m — tallest dam in Southeast Asia",
            "Reservoir: 695 km² (largest lake in Malaysia)",
            "8 × Francis turbines, 315 MW each",
            "Annual output: 15,000–16,500 GWh",
            "Construction cost: ~RM 7.4 billion"
        )
    ),
    MalaysianEnergySite(
        2, "Murum Dam", "Sarawak", EnergyType.HYDRO, 2.6467, 114.3658,
        "944 MW", "2014", "Murum Dam Sarawak hydropower",
        "Sarawak's second-largest hydro dam, powering the SCORE industrial corridor.",
        "Murum Dam sits on the Murum River, a tributary of the Rajang — Southeast Asia's longest river. The 141 m rockfill dam feeds four Francis turbines and primarily supplies power to SCORE industries. Together with Bakun and Batang Ai, it forms the backbone of Sarawak's hydropower portfolio.",
        "Water from the 245 km² Murum Reservoir passes through four vertical-shaft Francis turbines, each rated at 236 MW. Output feeds Sarawak's 275/500 kV transmission grid and dedicated SCORE industrial zones including the Samalaju Industrial Park.",
        listOf(
            "Reliable baseload power to SCORE industries",
            "Supports aluminium smelting with clean energy",
            "Low operational carbon footprint",
            "Reservoir enables water regulation during droughts",
            "Contributes to Sarawak's hydropower export capacity"
        ),
        "Murum supports SDG 7.1 and 7.2 by expanding Sarawak's renewable energy base, enabling industries to run on clean power rather than coal.",
        listOf(
            "Height: 141 m rockfill dam",
            "Reservoir: 245 km²",
            "4 × Francis turbines, 236 MW each",
            "Annual output: ~4,000 GWh",
            "SCORE anchor power supplier"
        )
    ),
    MalaysianEnergySite(
        3, "Pergau Dam", "Kelantan", EnergyType.HYDRO, 5.4330, 101.9960,
        "600 MW", "1996", "Pergau Dam Kelantan power station",
        "Peninsular Malaysia's largest hydro station, built with UK aid funding.",
        "Pergau Dam on the Pergau River is the largest hydroelectric station in Peninsular Malaysia. Despite controversy over its UK ODA-linked arms deal, it has operated reliably for 30+ years as a critical peaking power station for the National Grid.",
        "The 97 m concrete dam creates a reservoir at altitude. Water channels via underground tunnels to a cavern powerhouse with four Francis turbines. As a peaking plant it ramps up in ~10 minutes to meet demand spikes, reducing reliance on gas peakers.",
        listOf(
            "Peak-load management for Peninsular Malaysia's National Grid",
            "Rapid 10-minute ramp-up to meet demand spikes",
            "Reduces dependence on gas peaker plants",
            "Supplies clean power to rural Kelantan",
            "30+ years of operation with minimal fuel cost"
        ),
        "Pergau's peaking capability supports SDG 7.3 (energy efficiency) by enabling a more stable grid and reducing wasteful spinning reserve from fossil-fuel plants.",
        listOf(
            "Height: 97 m double-curvature arch dam",
            "4 × Francis turbines, 150 MW each",
            "Annual output: ~1,000 GWh (peaking plant)",
            "Underground cavern powerhouse",
            "Connected to Peninsular Grid at 275 kV"
        )
    ),
    MalaysianEnergySite(
        4, "Temenggor Dam", "Perak", EnergyType.HYDRO, 5.4057, 101.2985,
        "348 MW", "1978", "Temenggor Dam Royal Belum Perak",
        "Perak's iconic 45-year-old dam adjacent to Royal Belum rainforest.",
        "Temenggor Dam on the Perak River is Malaysia's oldest large hydroelectric dam, commissioned in 1978. Its reservoir borders Royal Belum State Park — home to 130-million-year-old rainforest. The dam and surrounding Tasik Temenggor have become a major ecotourism destination.",
        "A rockfill embankment dam creates Tasik Temenggor (152 km²). Water discharges through two intake towers into penstocks feeding four Francis turbines. Output transmits at 132/275 kV to TNB's northern grid. The dam has operated carbon-free for over 45 years.",
        listOf(
            "Clean electricity to Perak and northern Malaysia since 1978",
            "Tasik Temenggor supports ecotourism and fishing",
            "Adjacent to Royal Belum biodiversity hotspot",
            "Flood mitigation for Perak River communities",
            "45+ years of carbon-free electricity generation"
        ),
        "Temenggor demonstrates SDG 7's long-term vision: renewable infrastructure built for a 50-year lifecycle with minimal environmental impact compared to coal.",
        listOf(
            "Tasik Temenggor: 152 km²",
            "4 × Francis turbines, 87 MW each",
            "Operational since 1978 — 45+ years",
            "Annual output: ~1,200 GWh",
            "Borders 130-million-year-old Royal Belum rainforest"
        )
    ),
    MalaysianEnergySite(
        5, "Chenderoh Power Station", "Perak", EnergyType.HYDRO, 4.9610, 100.9780,
        "21 MW", "1930", "Chenderoh hydroelectric dam Perak colonial",
        "Malaysia's oldest hydroelectric station — built in 1930, still operating today.",
        "Chenderoh is Malaysia's first hydroelectric power station, built in 1930 by the British colonial government to supply Perak's tin-mining industry. After multiple TNB refurbishments it continues to serve the local grid — a 94-year demonstration of renewable energy infrastructure longevity.",
        "A low concrete weir on the Perak River raises water level by 10 m. Flow is directed through a channel to a run-of-river powerhouse with three refurbished generators. The run-of-river design means minimal ecological disruption — no large reservoir is created.",
        listOf(
            "Continuous electricity supply since 1930",
            "Heritage engineering landmark in Malaysia",
            "Minimal ecological disruption — no large reservoir",
            "Demonstrates 90+ year renewable infrastructure lifespan",
            "Continuous TNB refurbishments since independence"
        ),
        "Chenderoh illustrates SDG 7's historical dimension: clean energy infrastructure, if well-maintained, can serve communities for nearly 100 years.",
        listOf(
            "Built: 1930 (94 years of operation)",
            "3 × refurbished generators",
            "Run-of-river design — no large reservoir",
            "Malaysia's oldest active power station",
            "Multiple TNB refurbishments since 1957"
        )
    ),
    MalaysianEnergySite(
        6, "LSS Sepang", "Selangor", EnergyType.SOLAR, 2.7864, 101.6161,
        "50 MW", "2018", "LSS Sepang solar farm Selangor TNB panels",
        "Malaysia's first large-scale solar farm under the LSS programme, near KLIA.",
        "LSS Sepang is TNB's first large-scale solar farm, commissioned under LSS1 in November 2018. Located on 243 acres in Mukim Tanjung Dua Belas, Kuala Langat, it generated over 110,000 MWh in its first year — surpassing its declared annual quantity by 6%. It is the flagship project that proved Malaysia's solar business model.",
        "~150,000 monocrystalline silicon PV panels convert solar radiation to DC electricity. String inverters convert DC to AC at 33 kV, stepped up to 132 kV for injection into TNB's grid under a 21-year Power Purchase Agreement (PPA). Plant availability was 99% in Year 1.",
        listOf(
            "Powers ~15,000 Malaysian homes daily",
            "Avoids ~44,700 tonnes CO₂ per year",
            "99% plant availability in first year of operation",
            "Pioneer that proved Malaysia's LSS business model",
            "Creates skilled local operation and maintenance jobs"
        ),
        "LSS Sepang directly demonstrates SDG 7.2. Malaysia's LSS programme brought solar from near-zero to 1,933 MW by 2023 — LSS Sepang was the first step.",
        listOf(
            "Area: 243 acres, Mukim Tanjung Dua Belas",
            "~150,000 monocrystalline silicon PV panels",
            "First year output: >110,000 MWh (+6% above target)",
            "Plant availability: 99% in Year 1",
            "21-year PPA with TNB under LSS1 programme"
        )
    ),
    MalaysianEnergySite(
        7, "Pajam Solar Power Plant", "Negeri Sembilan", EnergyType.SOLAR, 2.8357, 101.8488,
        "30 MW", "2019", "Pajam solar park Negeri Sembilan bifacial panels",
        "LSS3 bifacial solar farm in Negeri Sembilan supplying TNB grid.",
        "The Pajam Solar Power Plant in Nilai, Negeri Sembilan, is an LSS3 facility using bifacial PV modules on fixed-tilt racking. It was developed by Superb Sunrise Sdn Bhd and sells all output to TNB under a 21-year PPA at a competitive LSS auction tariff.",
        "Bifacial panels capture direct sunlight from the front and diffuse/reflected radiation from the rear, boosting yield by 5–15% over monofacial panels. DC output flows through combiner boxes to central inverters and a 33 kV step-up transformer for grid injection.",
        listOf(
            "Bifacial technology boosts yield by up to 15%",
            "Provides stable daytime clean power to TNB grid",
            "Reduces grid reliance on natural gas for peak demand",
            "Contributes to Negeri Sembilan's clean energy profile",
            "Supports Malaysia's carbon neutrality by 2050 target"
        ),
        "Pajam supports SDG 7.2 and SDG 13 simultaneously. Each MW commissioned replaces gas generation, directly reducing Malaysia's electricity sector carbon intensity.",
        listOf(
            "Module type: bifacial PV, fixed-tilt",
            "Annual output: ~45 GWh",
            "CO₂ avoidance: ~33,000 tonnes/year",
            "LSS3 programme winner",
            "21-year PPA — fully grid-tied, no on-site storage"
        )
    ),
    MalaysianEnergySite(
        8, "LSS Bukit Selambau", "Kedah", EnergyType.SOLAR, 5.6704, 100.6270,
        "30 MW", "2020", "LSS Bukit Selambau TNB Kedah solar farm",
        "TNB's second solar farm — completed 114 days ahead of schedule in Kedah.",
        "LSS Bukit Selambau is TNB's second large-scale solar farm, completed 114 days ahead of schedule on 8 September 2020. Spread across 124 acres with 134,880 PV panels, it generates ~61,000 MWh annually and covers the daily energy demand of ~17,000 Malaysian homes.",
        "134,880 PV modules on fixed-tilt racking rows are spaced to allow rainfall to reach the ground. AC output at 33 kV connects to TNB's northern distribution network. Together with LSS Sepang, both farms save ~89,400 tonnes of CO₂ per year.",
        listOf(
            "134,880 PV panels generating clean electricity",
            "Completed 114 days ahead of original schedule",
            "Saves ~44,716 tonnes CO₂ per year",
            "Covers daily energy for ~17,000 homes",
            "Demonstrates rapid solar deployment in Malaysia"
        ),
        "Bukit Selambau showcases SDG 7 intersection with SDG 2: solar on agricultural land supplements rural income without displacing food production.",
        listOf(
            "Capacity: 30 MWac (45 MWdc)",
            "Panels: 134,880 PV modules across 124 acres",
            "Commissioned: 8 September 2020 (114 days early)",
            "Annual output: ~61,000 MWh",
            "TNB's second LSS project under LSS2 programme"
        )
    ),
    MalaysianEnergySite(
        9, "Rimba Terjun Solar Power Plant", "Johor", EnergyType.SOLAR, 1.4721, 103.3925,
        "50 MW", "2020", "Rimba Terjun solar Johor Malaysia tracker",
        "Johor's largest solar farm with single-axis trackers near Iskandar Malaysia.",
        "Rimba Terjun Solar Power Plant in Pontian, Johor, is one of Malaysia's southernmost utility-scale solar facilities. Near the Iskandar Malaysia economic zone, it supplies clean power to data centres, logistics hubs, and manufacturers demanding renewable energy. It uses single-axis solar trackers for maximum yield.",
        "Over 130,000 panels on single-axis trackers follow the sun east-to-west throughout the day, increasing energy capture by 15–25% vs fixed-tilt. A SCADA system monitors performance and output feeds the Johor 132 kV grid.",
        listOf(
            "Single-axis trackers increase yield by up to 25%",
            "Supplies renewable power to Iskandar Malaysia industries",
            "Enables data centres to meet green energy commitments",
            "Reduces Johor's gas plant dependency",
            "Southern Malaysia's strategic clean energy asset"
        ),
        "Rimba Terjun exemplifies SDG 7.a: private investment in clean energy supplying multinational manufacturers with Scope 2 carbon commitments.",
        listOf(
            "130,000+ panels on single-axis trackers",
            "Annual output: ~90 GWh",
            "Tracker yield gain: +20% vs fixed-tilt",
            "Supports corporate RE100 supply chains",
            "Connected to Johor 132 kV ring"
        )
    ),
    MalaysianEnergySite(
        10, "Suria Sungai Petani Solar Park", "Kedah", EnergyType.SOLAR, 5.6500, 100.4800,
        "29 MW", "2019", "Sungai Petani Kedah solar park mono PERC",
        "One of Kedah's earliest LSS plants — pioneer of northern Malaysia solar.",
        "Suria Sungai Petani Solar Park in Kuala Muda, Kedah, was built by Solarvest Holdings under Malaysia's LSS2 programme. It uses Mono-PERC (Passivated Emitter and Rear Cell) modules for higher efficiency and was commissioned ahead of schedule.",
        "Mono-PERC PV modules on fixed-tilt steel racking convert solar irradiance to DC. String inverters convert to three-phase AC feeding into the 33 kV local distribution network. An energy monitoring system records real-time output, weather data and performance indicators.",
        listOf(
            "Higher efficiency Mono-PERC panels",
            "Commissioned ahead of original schedule",
            "Reduces grid carbon intensity in Kedah",
            "Contributes to northern Malaysia RE portfolio",
            "Model for subsequent Solarvest LSS projects"
        ),
        "SDG 7.b calls for expanding energy infrastructure. Suria SP helped prove the financial model enabling Malaysia to scale solar from 200 MW (2016) to 1,933 MW (2023).",
        listOf(
            "Capacity: 29 MWac",
            "Area: 106 acres in Kuala Muda, Kedah",
            "Technology: Mono-PERC photovoltaic modules",
            "EPC contractor: Solarvest Holdings Berhad",
            "LSS2 programme — 21-year PPA with TNB"
        )
    ),
    MalaysianEnergySite(
        11, "Teluk Intan Biomass Plant", "Perak", EnergyType.BIOMASS, 4.0220, 101.0200,
        "12.5 MW", "2014", "Teluk Intan biomass power plant Perak palm EFB",
        "Malaysia's showcase palm waste biomass plant in Teluk Intan, Perak.",
        "The Teluk Intan Biomass Power Plant on an 18-acre site in Kampung Selabak uses Empty Fruit Bunches (EFB) from over 20 nearby palm oil mills. It processes up to 1,200 tonnes of EFB per day and has supplied clean electricity under a 21-year FiT PPA with TNB since 2014. Etagreen took over operations in 2022 with extensive upgrades.",
        "EFB is shredded, dried and fed into a direct combustion stoker boiler. Steam at high pressure drives a back-pressure turbine generating 12.5 MW. Ash by-products are returned to plantations as potassium-rich fertiliser, creating a true closed-loop circular economy.",
        listOf(
            "Processes up to 1,200 tonnes EFB per day",
            "Closed-loop: ash returns to plantations as fertiliser",
            "Eliminates open burning of agricultural waste",
            "Carbon-neutral biomass energy cycle",
            "21 years of clean electricity under FiT scheme"
        ),
        "Teluk Intan embodies SDG 7 and SDG 12 (Responsible Consumption): it converts an agricultural waste liability into affordable electricity — a model for Malaysia's 450+ palm oil mills.",
        listOf(
            "Capacity: 12.5 MW nett",
            "Feedstock: Empty Fruit Bunches (EFB) from oil palm",
            "Processing: up to 1,200 tonnes EFB/day",
            "Site: 18 acres, Kampung Selabak, Perak",
            "FiT PPA with TNB commenced August 2014"
        )
    ),
    MalaysianEnergySite(
        12, "Seguntor Bioenergy Plant", "Sabah", EnergyType.BIOMASS, 5.9500, 118.0500,
        "14 MW", "2015", "Seguntor bioenergy Sandakan Sabah palm biomass",
        "Sabah bioenergy plant converting Sandakan plantation waste to power.",
        "The Seguntor Bioenergy Plant near Sandakan sits in the heart of Sabah's oil palm belt. Developed under Malaysia's Feed-in Tariff (FiT) programme, it provides a permanent market for palm waste from mills previously relying on open burning or landfilling.",
        "Palm-derived biomass — EFB, palm fibre, and shells — is combusted in a travelling grate boiler. High-pressure steam passes through a condensing turbine maximising electrical output. Continuous emissions monitoring ensures compliance with Sabah's Environmental Quality Act standards.",
        listOf(
            "Reliable baseload power to Sandakan grid",
            "Reduces open burning of palm waste in Sabah",
            "Employs local workforce in plant and feedstock logistics",
            "Demonstrates circular economy in palm oil industry",
            "Supports Sabah's SE-RAMP 2040 energy roadmap"
        ),
        "Seguntor supports SDG 7.1 by bringing affordable, reliable electricity to Sabah through local renewable resources rather than expensive diesel imports.",
        listOf(
            "Fuel: EFB, palm fibre, palm shells",
            "Boiler: travelling grate technology",
            "Annual output: ~95 GWh",
            "FiT tariff: RM 0.27/kWh",
            "Continuous emissions monitoring installed"
        )
    ),
    MalaysianEnergySite(
        13, "TSH Bio Energy Plant", "Sabah", EnergyType.BIOMASS, 5.2500, 118.3500,
        "20 MW", "2013", "TSH bioenergy Sabah palm plantation CHP",
        "One of Sabah's largest biomass CHP plants, by TSH Resources Berhad.",
        "TSH Bio Energy Plant in Tawau, Sabah, is operated by TSH Resources Berhad — a vertically integrated palm oil company. Plantation waste directly powers TSH's own facilities in a Combined Heat and Power (CHP) configuration, with surplus electricity exported to Sabah Electricity Sdn Bhd (SESB).",
        "Palm waste from TSH's own mills is combusted in a Bubbling Fluidised Bed (BFB) boiler. Steam drives a 20 MW turbine and provides process heat to the mill — a CHP system achieving 70–85% fuel efficiency vs 35% for power-only plants. Surplus electricity is exported to the Sabah grid.",
        listOf(
            "Combined Heat and Power (CHP) — 70–85% fuel efficiency",
            "Fully integrated palm-to-power circular model",
            "Captive feedstock eliminates supply uncertainty",
            "Surplus power exported to SESB Sabah grid",
            "Demonstrates corporate sustainability at plantation scale"
        ),
        "TSH's model aligns with SDG 7 and SDG 9 (Industry/Innovation). CHP biomass systems double energy efficiency over conventional power plants.",
        listOf(
            "Capacity: ~20 MW",
            "Configuration: Combined Heat and Power (CHP)",
            "Boiler: Bubbling Fluidised Bed (BFB)",
            "Operator: TSH Resources Berhad (listed company)",
            "Commissioned 2013 under FiT regime"
        )
    ),
    MalaysianEnergySite(
        14, "Jana Landfill Biogas Plant", "Selangor", EnergyType.BIOMASS, 2.9300, 101.5700,
        "6 MW", "2012", "Jana biogas landfill gas Selangor Malaysia",
        "Captures harmful landfill methane and converts it to clean electricity.",
        "Jana Landfill Biogas Plant in Selangor captures methane from decomposing municipal waste. Landfill gas is ~50–60% methane (CH₄). Without capture, methane escapes with 28× the warming potential of CO₂. Jana converts this liability into electricity under a CDM-registered project.",
        "Perforated HDPE pipes drilled into the landfill draw biogas to a central collection header under negative pressure. Gas is cleaned of H₂S and moisture, then fed to two Jenbacher gas engines driving synchronous generators at 415 V, stepped up to 33 kV for TNB grid injection.",
        listOf(
            "Prevents potent methane (28× CO₂) from escaping",
            "Converts environmental hazard into clean electricity",
            "CDM-eligible carbon credits",
            "Reduces landfill odour through controlled extraction",
            "Supplies affordable power to Selangor communities"
        ),
        "Jana demonstrates SDG 7 and SDG 13 convergence: every 6 MWh generated avoids ~30 tonnes of CO₂-equivalent by capturing methane before it escapes.",
        listOf(
            "Gas: 50–60% CH₄ from municipal landfill",
            "2 × Jenbacher gas engines",
            "Annual output: ~42 GWh",
            "CH₄ avoided: ~15,000 t/year (420,000 t CO₂-eq)",
            "CDM registered project"
        )
    ),
    MalaysianEnergySite(
        15, "Kudat Wind Farm", "Sabah", EnergyType.WIND, 6.8413, 116.7492,
        "30 MW pilot", "2018", "Kudat wind turbine Sabah Malaysia tip Borneo",
        "Malaysia's first wind farm at the northernmost tip of Borneo.",
        "Kudat Wind Farm — the Berjaya Wind Farm — is Malaysia's first commercial wind energy project, located near the Tip of Borneo. Sabah Energy Corporation (SEC) is now conducting a feasibility study for a 100 MW utility-scale wind expansion, which would be Malaysia's largest wind installation. Kudat has the highest wind energy potential in Malaysia.",
        "Horizontal-axis three-blade turbines capture kinetic energy from Northeast Monsoon winds. The rotor drives a gearbox connected to a Doubly-Fed Induction Generator (DFIG) at variable speed. A power converter matches variable output to grid frequency (50 Hz).",
        listOf(
            "Malaysia's first commercial onshore wind farm",
            "Northeast Monsoon provides 5–6 months of reliable generation",
            "Kudat identified as Malaysia's highest wind potential site",
            "SEC studying 100 MW expansion — up from 30 MW pilot",
            "Pioneers wind as Malaysia's third renewable energy pillar"
        ),
        "Kudat supports SDG 7's diversification imperative. Malaysia has focused on hydro and solar; Kudat builds the case for wind as a third renewable pillar, enhancing long-term energy security.",
        listOf(
            "Pilot capacity: 30 MW (Berjaya Wind Farm)",
            "Planned expansion: up to 100 MW (SEC 2025 study)",
            "Average wind speed: 5–7 m/s at hub height",
            "Cut-in speed: 3.5 m/s | Rated: 12 m/s",
            "Best generation window: November–March (NE Monsoon)"
        )
    ),
    MalaysianEnergySite(
        16, "Mersing Wind Monitoring Station", "Johor", EnergyType.WIND, 2.4300, 103.8400,
        "Monitoring site", "2015", "Mersing offshore wind Johor South China Sea anemometer",
        "Wind monitoring station for Malaysia's first offshore wind farm.",
        "The Mersing monitoring station on Johor's South China Sea coast captures wind resource data on one of Malaysia's most promising offshore wind corridors. Mersing was identified by TNB and the Energy Commission under the National Industrial Master Plan 2030 as the candidate zone for Malaysia's first offshore wind farm.",
        "A 100 m meteorological mast with cup anemometers at multiple heights transmits continuous wind data via 4G/satellite. Wave height and ocean current sensors are deployed for offshore foundation feasibility studies. Data is used to produce bankable energy yield assessments for project financing.",
        listOf(
            "Generates 2+ years of bankable wind resource data",
            "Validates Malaysia's offshore wind potential to investors",
            "Informs turbine hub height and spacing design",
            "Part of Malaysia's 2 GW offshore wind roadmap by 2035",
            "NE Monsoon delivers 6–8 m/s winds Oct–Mar"
        ),
        "Pre-development monitoring is the critical SDG 7 first step. Without validated data, projects cannot attract investment financing — Mersing's dataset enables Malaysia's offshore wind future.",
        listOf(
            "100 m met-mast with multi-height anemometry",
            "Average wind speed: 6–8 m/s (NE Monsoon)",
            "Target: Malaysia's first offshore wind farm",
            "Under NIMP 2030 wind roadmap",
            "TNB and Energy Commission backed assessment"
        )
    ),
    MalaysianEnergySite(
        17, "Pulau Layang-Layang Wind Research", "Sabah Waters", EnergyType.WIND, 7.3700, 113.8400,
        "Research site", "2017", "Layang Layang atoll South China Sea wind Malaysia",
        "Remote Spratly atoll wind research site for offshore energy feasibility.",
        "Pulau Layang-Layang (Swallow Reef) is a Malaysian-administered atoll 300 km northwest of Kota Kinabalu in the South China Sea. Open-ocean winds from multiple directions and strong NE Monsoon conditions make this one of Malaysia's highest-quality wind resource sites. Research here informs Malaysia's long-term deep-water offshore wind strategy.",
        "Instrumented meteorological buoys and offshore met-masts measure wind at hub heights up to 100 m. Data is transmitted via satellite to Sabah research institutions. Wave, current, and seabed surveys assess feasibility for fixed-bottom or floating offshore turbines in 20–50 m water depth.",
        listOf(
            "Highest wind speeds in Malaysian territorial waters",
            "Informs potential for deep-water offshore wind",
            "Open ocean avoids land-use conflicts",
            "Supports Malaysia's deep-water energy strategy",
            "Academic research with UPM, UTM and international partners"
        ),
        "Layang-Layang addresses SDG 7's long-term frontier: floating offshore wind could unlock hundreds of GW from Malaysia's South China Sea EEZ by 2035–2040.",
        listOf(
            "Location: Spratly Islands, South China Sea",
            "Distance: ~300 km NW of Kota Kinabalu",
            "Water depth: 20–50 m (fixed-bottom viable)",
            "NE Monsoon avg. wind speed: 8–10 m/s",
            "Research phase — not yet commercial"
        )
    ),
    MalaysianEnergySite(
        18, "Ulu Jelai Hydropower", "Pahang", EnergyType.HYDRO, 4.1500, 101.6000,
        "372 MW", "2015", "Ulu Jelai hydropower Cameron Highlands Pahang",
        "Cameron Highlands pumped-storage hydro plant in Pahang.",
        "Ulu Jelai Hydroelectric Project in Cameron Highlands, Pahang, is Malaysia's first pumped-storage hydroelectric power plant. During off-peak hours it pumps water uphill to a reservoir; during peak demand it releases water back through turbines, acting as a giant battery for the national grid.",
        "During off-peak periods, surplus grid electricity pumps water from the lower Ringlet Reservoir to the upper Ulu Jelai Reservoir. During peak demand, water descends through four reversible Francis pump-turbines, generating up to 372 MW within minutes. This load-balancing is critical as Malaysia adds variable solar and wind capacity.",
        listOf(
            "Malaysia's first pumped-storage hydro — a grid-scale battery",
            "Stores surplus off-peak electricity for peak dispatch",
            "Enables more solar/wind without grid instability",
            "Rapid response to demand spikes in minutes",
            "Reduces need for expensive gas peaker plants"
        ),
        "Ulu Jelai underpins SDG 7.3 (energy efficiency) and SDG 7.2 by enabling Malaysia to integrate more variable renewable energy safely into the grid.",
        listOf(
            "Capacity: 372 MW (4 × 93 MW reversible units)",
            "Type: Pumped-storage hydroelectric",
            "Upper reservoir: Ulu Jelai | Lower: Ringlet",
            "Response time: <3 minutes to full output",
            "Malaysia's only pumped-storage facility"
        )
    )
)

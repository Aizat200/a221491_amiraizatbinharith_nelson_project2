package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// Shared data classes (used by TopicScreen & QuizScreen)
// ─────────────────────────────────────────────────────────────────────────────
data class SectionData(
    val heading   : String,
    val body      : String,
    val callout   : String?                    = null,
    val highlights: List<Pair<String, String>>? = null,
    val flowSteps : List<List<String>>?         = null,
    val stats     : List<Pair<String, String>>? = null
)

data class QuizData(
    val question    : String,
    val options     : List<String>,
    val correctIndex: Int
)

data class ChapterData(
    val number  : Int,
    val title   : String,
    val subtitle: String,
    val sections: List<SectionData>,
    val quiz    : QuizData
)

data class TopicMeta(
    val index   : Int,
    val emoji   : String,
    val title   : String,
    val subtitle: String,
    val iconBg  : Color,
    val chapters: List<ChapterData>
)

// ─────────────────────────────────────────────────────────────────────────────
// SOLAR ENERGY (index 0)
// ─────────────────────────────────────────────────────────────────────────────
private val solarChapters = listOf(
    ChapterData(
        number   = 1,
        title    = "What is solar energy?",
        subtitle = "An introduction to the sun as an energy source and why it matters.",
        sections = listOf(
            SectionData(
                heading = "The sun as a power source",
                body    = "The sun radiates an enormous amount of energy every second — far more than humanity currently consumes in an entire year. This energy travels 150 million kilometres through space as electromagnetic radiation before reaching Earth's surface, where it can be captured and converted into useful electricity or heat.\n\nSolar energy is a renewable resource because the sun will continue shining for another ~5 billion years. Unlike coal or natural gas, using sunlight does not deplete any finite reserve.",
                callout = "Key fact: In just 90 minutes, enough sunlight strikes Earth's surface to power the entire world's energy consumption for a full year."
            ),
            SectionData(
                heading   = "Two main ways to use sunlight",
                body      = "Solar energy can be harvested as light (photovoltaics convert sunlight directly to electricity) or as heat (solar thermal systems heat water or drive turbines). This textbook focuses primarily on the photovoltaic route.",
                flowSteps = listOf(
                    listOf("☀️ Sunlight", "Photovoltaic cell", "⚡ Electricity"),
                    listOf("☀️ Sunlight", "Thermal collector", "🔥 Heat / Steam")
                )
            ),
            SectionData(
                heading    = "Advantages and limitations",
                body       = "Like every energy source, solar has trade-offs engineers and policymakers must weigh carefully.",
                highlights = listOf(
                    "Clean ✅"        to "No greenhouse gas emissions during operation",
                    "Abundant ✅"     to "Available on every continent on Earth",
                    "Intermittent ⚠️" to "Output drops at night and on cloudy days",
                    "Land use ⚠️"    to "Large-scale farms require significant area"
                )
            )
        ),
        quiz = QuizData(
            question     = "Which best describes why solar energy is considered renewable?",
            options      = listOf(
                "Solar panels last forever without degrading",
                "Sunlight arrives continuously and is not depleted by use",
                "The sun produces more energy than any other star",
                "Solar cells generate power even underground"
            ),
            correctIndex = 1
        )
    ),
    ChapterData(
        number   = 2,
        title    = "How photovoltaic cells work",
        subtitle = "A step-by-step look at the physics behind converting light into current.",
        sections = listOf(
            SectionData(
                heading = "The photovoltaic effect",
                body    = "Discovered by Edmond Becquerel in 1839, the photovoltaic (PV) effect is the phenomenon where certain materials generate an electric voltage when exposed to light. Modern solar cells exploit this using semiconductor materials, most commonly silicon.",
                callout = "Etymology: \"Photo\" comes from the Greek for light (φῶς); \"voltaic\" refers to electricity — named after Alessandro Volta, inventor of the battery."
            ),
            SectionData(
                heading   = "Inside a silicon solar cell",
                body      = "A silicon solar cell is a thin semiconductor sandwich. The top layer is doped with phosphorus to create extra free electrons (n-type), while the bottom layer is doped with boron creating electron-shaped holes (p-type). Their boundary is the p-n junction.\n\nWhen photons strike the cell, they knock electrons loose at the junction. The built-in electric field forces them in one direction — producing direct current (DC).",
                flowSteps = listOf(
                    listOf("☀️ Photon", "Knocks electron loose", "Electron travels circuit", "⚡ DC current")
                )
            ),
            SectionData(
                heading = "From cell to grid",
                body    = "A single cell produces ~0.5 V. Cells connect in series to form a module (panel); modules link into an array. An inverter converts DC to AC for home appliances and the electricity grid.",
                stats   = listOf(
                    "~15%"   to "Early panels (1990s)",
                    "20–22%" to "Modern residential",
                    "47%"    to "Lab record (multi-junction)"
                )
            )
        ),
        quiz = QuizData(
            question     = "What is the role of the inverter in a solar power system?",
            options      = listOf(
                "It stores excess energy in batteries",
                "It increases the voltage of individual cells",
                "It converts direct current (DC) to alternating current (AC)",
                "It filters dust off the panels automatically"
            ),
            correctIndex = 2
        )
    ),
    ChapterData(
        number   = 3,
        title    = "Types of solar panel systems",
        subtitle = "Key configurations and when each is best used.",
        sections = listOf(
            SectionData(
                heading    = "Three cell technologies",
                body       = "Monocrystalline panels (single crystal) are the most efficient but cost more. Polycrystalline (melted silicon fragments) are cheaper but slightly less efficient. Thin-film deposits semiconductor on glass or metal — flexible but lower efficiency.",
                highlights = listOf(
                    "Monocrystalline" to "17–22% · premium cost · best for limited roof space",
                    "Polycrystalline" to "15–18% · mid-range cost · common residential use",
                    "Thin-film CdTe"  to "10–13% · lowest cost · suited for large flat areas",
                    "Thin-film CIGS"  to "13–15% · flexible form · used in portable devices"
                )
            ),
            SectionData(
                heading = "Grid-tied vs. off-grid systems",
                body    = "A grid-tied system exports surplus power to the public grid and draws from it when solar generation is insufficient — the most common home setup.\n\nAn off-grid system is fully independent with a battery bank, ideal for remote locations with no grid access.",
                callout = "Hybrid systems store excess solar in batteries first; only truly surplus power is exported — maximising self-consumption and providing backup during outages."
            ),
            SectionData(
                heading   = "Scale: rooftop to utility",
                body      = "Solar spans an enormous range of scales, from a home rooftop system to multi-megawatt utility farms.",
                flowSteps = listOf(
                    listOf("🏠 Rooftop\n4–10 kW", "🏢 Commercial\n100–500 kW", "🌅 Solar farm\n1–1,000 MW", "🔌 Grid supply")
                )
            )
        ),
        quiz = QuizData(
            question     = "Which solar panel type suits a home with a small roof but high energy needs?",
            options      = listOf(
                "Thin-film — the cheapest option available",
                "Monocrystalline — highest efficiency per square metre",
                "Polycrystalline — works better in partial shade",
                "CIGS thin-film — flexible enough to bend around the roof"
            ),
            correctIndex = 1
        )
    ),
    ChapterData(
        number   = 4,
        title    = "Solar worldwide & SDG 7",
        subtitle = "How solar is reshaping the global energy landscape.",
        sections = listOf(
            SectionData(
                heading = "The solar revolution in numbers",
                body    = "Solar has become the fastest-growing energy technology in history. Its cost has fallen over 90% since 2010, making it the cheapest new electricity source in most of the world as of 2023.",
                stats   = listOf(
                    ">90%"      to "Cost drop since 2010",
                    "1,000+ GW" to "Global capacity",
                    "4.9%"      to "Global electricity share (2023)",
                    "~40 Mt"    to "CO₂ avoided annually"
                )
            ),
            SectionData(
                heading = "Malaysia context",
                body    = "Malaysia receives ~4–6 kWh/m² of solar irradiance per day — well above the global average — making it ideal for solar deployment.",
                callout = "The National Energy Transition Roadmap (NETR) targets 70% renewable energy by 2050, with solar playing a central role."
            ),
            SectionData(
                heading    = "SDG 7 — Affordable and clean energy",
                body       = "UN SDG 7 calls for universal access to affordable, reliable, sustainable, and modern energy by 2030. Solar is central to this — especially for the 733 million people worldwide still without electricity access.",
                highlights = listOf(
                    "Target 7.1" to "Universal energy access — off-grid solar electrifies remote areas",
                    "Target 7.2" to "Increase renewable share — solar is the fastest-growing clean source",
                    "Target 7.3" to "Double efficiency — modern panels achieve 20%+ efficiency"
                )
            )
        ),
        quiz = QuizData(
            question     = "Which SDG target does off-grid solar most directly support in developing nations?",
            options      = listOf(
                "Target 7.1 — ensuring universal access to modern energy services",
                "Target 7.3 — doubling global energy efficiency improvements",
                "SDG 13 — taking action on climate change only",
                "SDG 9 — building resilient infrastructure in wealthy countries"
            ),
            correctIndex = 0
        )
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// WIND ENERGY (index 1)
// ─────────────────────────────────────────────────────────────────────────────
private val windChapters = listOf(
    ChapterData(
        number   = 1,
        title    = "What is wind energy?",
        subtitle = "Understanding how moving air becomes electricity.",
        sections = listOf(
            SectionData(
                heading = "The basics of wind",
                body    = "Wind is created by the uneven heating of Earth's surface by the sun. Warm air rises and cooler air rushes in to take its place, creating the air currents we call wind. This kinetic energy has been harnessed by humans for thousands of years — first for sailing and milling grain, now for generating electricity at massive scale.\n\nWind power is clean, renewable, and one of the fastest-growing energy sources globally. It produced about 7% of global electricity in 2023.",
                callout = "Key fact: Wind turbines can begin generating power at wind speeds as low as 3 m/s (about 11 km/h) and typically reach peak output at 12–15 m/s."
            ),
            SectionData(
                heading    = "Onshore vs offshore wind",
                body       = "Wind farms can be built on land (onshore) or at sea (offshore). Each approach has distinct advantages.",
                highlights = listOf(
                    "Onshore ✅"  to "Lower cost · easier to maintain · widely deployed",
                    "Onshore ⚠️"  to "Noise and visual impact · lower average wind speed",
                    "Offshore ✅" to "Stronger, steadier winds · less visual impact",
                    "Offshore ⚠️" to "Expensive installation · complex maintenance at sea"
                )
            ),
            SectionData(
                heading   = "How a wind turbine works — overview",
                body      = "A wind turbine converts kinetic energy to rotational motion, then to electricity. The key steps are straightforward.",
                flowSteps = listOf(
                    listOf("💨 Wind", "Spins blades", "Rotor → gearbox", "Generator", "⚡ Grid")
                )
            )
        ),
        quiz = QuizData(
            question     = "What is the primary cause of wind on Earth?",
            options      = listOf(
                "The rotation of the Earth spinning the atmosphere",
                "Uneven heating of Earth's surface by the sun",
                "Gravity pulling air from high altitudes down",
                "Ocean currents pushing air masses along coastlines"
            ),
            correctIndex = 1
        )
    ),
    ChapterData(
        number   = 2,
        title    = "Inside a wind turbine",
        subtitle = "Anatomy and engineering of modern turbines.",
        sections = listOf(
            SectionData(
                heading = "Key components",
                body    = "A modern horizontal-axis wind turbine consists of several engineered systems working in concert. The rotor blades — typically three — are precision-shaped aerofoils, similar to aircraft wings. They capture kinetic energy from the wind and transfer it to the main shaft.",
                stats   = listOf(
                    "80–120 m" to "Typical tower height",
                    "50–80 m"  to "Blade length (onshore)",
                    "~5 MW"    to "Typical offshore turbine",
                    "25 years" to "Design lifetime"
                )
            ),
            SectionData(
                heading = "The nacelle — the heart of the turbine",
                body    = "Inside the nacelle (the housing at the top of the tower) sits the gearbox, which steps up the slow blade rotation to the high speed needed by the generator. Variable-speed turbines use power electronics instead, allowing them to extract maximum energy across a range of wind speeds.",
                callout = "Modern direct-drive turbines eliminate the gearbox entirely, using permanent-magnet generators that rotate slowly. This reduces mechanical failure points and maintenance costs."
            ),
            SectionData(
                heading   = "From shaft to grid",
                body      = "The generator produces AC electricity. A transformer steps the voltage up for transmission over the grid. The whole process from wind to electrons takes milliseconds.",
                flowSteps = listOf(
                    listOf("Rotor blades", "Main shaft", "Gearbox", "Generator", "Transformer", "⚡ Grid")
                )
            )
        ),
        quiz = QuizData(
            question     = "What is the role of the gearbox in a conventional wind turbine?",
            options      = listOf(
                "It converts AC electricity to DC for storage",
                "It adjusts the angle of the blades to face the wind",
                "It steps up the slow blade rotation speed for the generator",
                "It measures wind speed and stops the turbine in storms"
            ),
            correctIndex = 2
        )
    ),
    ChapterData(
        number   = 3,
        title    = "Wind power globally",
        subtitle = "The scale of wind energy deployment worldwide.",
        sections = listOf(
            SectionData(
                heading = "Global wind capacity",
                body    = "Wind energy has grown at an extraordinary pace. By 2024, global installed capacity exceeded 1,100 GW, enough to power hundreds of millions of homes. China, the United States, Germany, India, and Spain are the leading markets.",
                stats   = listOf(
                    "1,100+ GW" to "Global capacity (2024)",
                    "7%"        to "Global electricity (2023)",
                    "China #1"  to "Largest wind market",
                    "~2 million" to "People employed in wind sector"
                )
            ),
            SectionData(
                heading = "Wind energy and the environment",
                body    = "Wind turbines produce zero emissions during operation and have a very small carbon footprint over their full lifecycle — typically offset within 3–6 months of operation. Key environmental considerations include bird and bat collisions, noise, and visual impact on landscapes.",
                callout = "Life-cycle analysis: Wind power emits ~7–15 gCO₂eq/kWh — compared to ~820 gCO₂eq/kWh for coal, making it one of the cleanest energy sources."
            ),
            SectionData(
                heading    = "Challenges and solutions",
                body       = "Despite rapid growth, wind faces real challenges that engineers and planners are actively solving.",
                highlights = listOf(
                    "Variability"      to "Grid storage and demand management balance output",
                    "Grid integration" to "Smart grids and interconnectors link diverse regions",
                    "Social acceptance" to "Community ownership models share benefits locally",
                    "End-of-life blades" to "Recyclable blade materials being developed"
                )
            )
        ),
        quiz = QuizData(
            question     = "Approximately how much CO₂ does wind power emit over its lifetime per kWh?",
            options      = listOf(
                "820 gCO₂eq/kWh — similar to coal",
                "7–15 gCO₂eq/kWh — very low lifecycle emissions",
                "200 gCO₂eq/kWh — moderate, similar to gas",
                "Zero — there are absolutely no emissions at all"
            ),
            correctIndex = 1
        )
    ),
    ChapterData(
        number   = 4,
        title    = "Wind energy & SDG 7",
        subtitle = "Wind power's role in sustainable development goals.",
        sections = listOf(
            SectionData(
                heading    = "Wind's contribution to SDG 7",
                body       = "SDG 7 calls for affordable, reliable, sustainable, and modern energy for all by 2030. Wind energy contributes across all three sub-targets.",
                highlights = listOf(
                    "Target 7.1" to "Off-grid wind turbines can power remote rural communities",
                    "Target 7.2" to "Wind is one of the largest contributors to renewable electricity",
                    "Target 7.3" to "Turbine efficiency has roughly doubled in 20 years"
                )
            ),
            SectionData(
                heading = "Malaysia's wind potential",
                body    = "Malaysia has modest onshore wind resources (avg 2–3 m/s) but significant offshore potential, especially in Sabah and Sarawak. The government is exploring offshore wind as part of the National Energy Transition Roadmap (NETR) which targets 70% renewables by 2050.",
                callout = "A single modern 5 MW offshore turbine can generate enough electricity to power ~1,500 average Malaysian homes for a year."
            ),
            SectionData(
                heading   = "The future of wind",
                body      = "Floating offshore wind turbines — anchored in deep water — open up vast new ocean areas for development. Airborne wind energy systems fly kites or drones at high altitudes where winds are stronger and steadier. Both technologies are advancing rapidly.",
                flowSteps = listOf(
                    listOf("🌊 Floating offshore", "🪁 Airborne kites", "🔋 Grid + storage", "🌍 Clean energy future")
                )
            )
        ),
        quiz = QuizData(
            question     = "What innovative technology allows wind turbines to be placed in deep water far from shore?",
            options      = listOf(
                "Submerged turbines that spin underwater currents",
                "Floating offshore wind platforms anchored to the seabed",
                "Giant kites attached to onshore generators",
                "Solar-wind hybrid panels on ocean buoys"
            ),
            correctIndex = 1
        )
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// HYDROPOWER (index 2)
// ─────────────────────────────────────────────────────────────────────────────
private val hydroChapters = listOf(
    ChapterData(
        number   = 1,
        title    = "What is hydropower?",
        subtitle = "Using the energy of moving and falling water.",
        sections = listOf(
            SectionData(
                heading = "Water as an energy source",
                body    = "Hydropower harnesses the potential energy stored in water at height. When water flows downhill — through a dam or a natural river gradient — that energy can drive turbines connected to generators. Hydropower is the world's largest source of renewable electricity, supplying about 16% of global power and over 60% of all renewable electricity.",
                callout = "Hydropower is often described as 'batteries of the mountains' — water stored in reservoirs is effectively stored energy that can be released on demand."
            ),
            SectionData(
                heading    = "Types of hydropower",
                body       = "Not all hydropower involves massive dams. A spectrum of technologies suits different locations.",
                highlights = listOf(
                    "Large-scale dams"    to "Reservoir stores water; reliable but disrupts ecosystems",
                    "Run-of-river"        to "Diverts flowing water; minimal storage; less impact",
                    "Pumped-storage"      to "Pumps water uphill when surplus power exists; releases when demand peaks",
                    "Micro-hydro (<1 MW)" to "Powers remote villages using small streams"
                )
            ),
            SectionData(
                heading   = "How a dam generates electricity",
                body      = "The physics are elegant: gravity pulls stored water through penstocks (large pipes), spinning turbines at the bottom of the dam. The generator converts rotational energy to AC electricity.",
                flowSteps = listOf(
                    listOf("💧 Reservoir", "Penstock pipe", "Turbine spins", "Generator", "⚡ Grid")
                )
            )
        ),
        quiz = QuizData(
            question     = "What type of hydropower stores electricity by pumping water uphill during low-demand periods?",
            options      = listOf(
                "Run-of-river hydropower",
                "Micro-hydro systems",
                "Pumped-storage hydropower",
                "Tidal barrage systems"
            ),
            correctIndex = 2
        )
    ),
    ChapterData(
        number   = 2,
        title    = "Engineering a hydropower dam",
        subtitle = "How dams are designed, built and operated.",
        sections = listOf(
            SectionData(
                heading = "Dam types and design",
                body    = "Engineers choose dam types based on geology, river flow, and purpose. Gravity dams use their own weight to resist water pressure. Arch dams curve to transmit force sideways into canyon walls. Embankment dams use compacted earth or rock fill.\n\nThe turbines inside are matched to the site — Kaplan turbines suit low heads and high flow; Pelton wheels handle high heads and low flow; Francis turbines cover the middle range.",
                stats   = listOf(
                    "22,500 MW" to "Three Gorges Dam capacity",
                    "2,400 MW"  to "Bakun Dam, Sarawak (Malaysia)",
                    "60%"       to "Typical conversion efficiency",
                    "100+ years" to "Design lifetime of large dams"
                )
            ),
            SectionData(
                heading = "Environmental and social impacts",
                body    = "Large dams can displace communities, flood ecosystems, and block fish migration. The Three Gorges Dam in China displaced over 1.2 million people. Modern projects require thorough environmental impact assessments and increasingly include fish ladders, minimum flow requirements, and community compensation.",
                callout = "Sarawak's Bakun Dam reservoir covers an area larger than Singapore, illustrating both the power and the land-use footprint of large hydropower projects."
            ),
            SectionData(
                heading   = "Operation and grid services",
                body      = "Hydropower plants can ramp output up or down within seconds, making them ideal for balancing the variable output of solar and wind. Pumped-storage is currently the dominant form of grid-scale energy storage worldwide.",
                flowSteps = listOf(
                    listOf("☀️ Solar surplus", "Pump water uphill", "💧 Stored potential", "Release → turbine", "⚡ Peak demand")
                )
            )
        ),
        quiz = QuizData(
            question     = "Which turbine type is best suited for high water head (height) and relatively low water flow?",
            options      = listOf(
                "Kaplan turbine — designed for low-head, high-flow sites",
                "Pelton wheel — designed for high-head, low-flow sites",
                "Francis turbine — only works underground",
                "Archimedes screw — used only in tidal applications"
            ),
            correctIndex = 1
        )
    ),
    ChapterData(
        number   = 3,
        title    = "Hydropower around the world",
        subtitle = "Global scale and regional highlights.",
        sections = listOf(
            SectionData(
                heading = "A global powerhouse",
                body    = "Hydropower generates more electricity than all other renewables combined. It is the backbone of grids in Brazil, Canada, Norway, and many developing nations. Norway generates over 90% of its electricity from hydropower.",
                stats   = listOf(
                    "16%"      to "Global electricity share",
                    "Norway"   to "90%+ hydro-powered",
                    "Brazil"   to "65%+ hydro-powered",
                    "4,700 TWh" to "Global annual production"
                )
            ),
            SectionData(
                heading = "Hydropower in Malaysia",
                body    = "Malaysia's mountainous interior and high rainfall make it ideal for hydropower. Sarawak is home to several large dams including Bakun (2,400 MW) and Murum (944 MW), which form part of the Sarawak Corridor of Renewable Energy (SCORE) project — aiming to attract energy-intensive industries with clean, affordable power.",
                callout = "The SCORE initiative positions Sarawak as a renewable energy export hub, with plans to transmit clean hydropower to Peninsular Malaysia and potentially Singapore."
            ),
            SectionData(
                heading    = "Challenges and the future",
                body       = "Despite its advantages, hydropower expansion faces significant headwinds. Environmental groups oppose new large dams, and climate change is altering river flows. The focus is shifting to optimising existing dams and deploying small-scale and run-of-river systems.",
                highlights = listOf(
                    "Sedimentation"   to "Reservoirs fill with silt over decades, reducing capacity",
                    "Climate change"  to "Changing rainfall patterns create uncertainty",
                    "Small hydro"     to "Less impact; growing deployment in rural areas",
                    "Modernisation"   to "Upgrading turbines in old dams adds GW with no new flooding"
                )
            )
        ),
        quiz = QuizData(
            question     = "Which country generates over 90% of its electricity from hydropower?",
            options      = listOf(
                "Brazil",
                "China",
                "Norway",
                "Malaysia"
            ),
            correctIndex = 2
        )
    ),
    ChapterData(
        number   = 4,
        title    = "Hydropower & SDG 7",
        subtitle = "Water, energy and sustainable development.",
        sections = listOf(
            SectionData(
                heading    = "Hydro and the SDGs",
                body       = "Hydropower is already the world's leading renewable electricity source, and it has unique properties that contribute to multiple UN Sustainable Development Goals.",
                highlights = listOf(
                    "SDG 6"      to "Water management — dams also provide irrigation and flood control",
                    "SDG 7.1"    to "Micro-hydro electrifies off-grid communities cost-effectively",
                    "SDG 7.2"    to "World's largest renewable electricity source by volume",
                    "SDG 13"     to "Zero operating emissions; displaces fossil fuel generation"
                )
            ),
            SectionData(
                heading = "Energy access and rural development",
                body    = "Micro-hydropower systems (under 100 kW) have electrified thousands of remote villages in Nepal, Peru, and Indonesia — providing light, refrigeration for medicine, and power for small industries with minimal environmental impact.",
                callout = "A 10 kW micro-hydro system costing under RM 50,000 can power an entire rural Malaysian village, lasting 30+ years with minimal maintenance."
            ),
            SectionData(
                heading   = "The water-energy-food nexus",
                body      = "Dams do more than generate power. They store water for drinking and irrigation, control floods, and enable navigation. This multi-purpose value makes large dams complex to evaluate — benefits and costs are spread across many sectors and communities.",
                flowSteps = listOf(
                    listOf("💧 Water storage", "⚡ Power generation", "🌾 Irrigation", "🏘️ Flood control", "🌍 SDG progress")
                )
            )
        ),
        quiz = QuizData(
            question     = "Beyond electricity, which additional benefits do large hydropower dams typically provide?",
            options      = listOf(
                "They produce hydrogen fuel as a by-product of generation",
                "They provide irrigation water, flood control, and drinking water storage",
                "They capture carbon dioxide dissolved in the reservoir water",
                "They generate geothermal heat from the dam foundations"
            ),
            correctIndex = 1
        )
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// BIOMASS ENERGY (index 3)
// ─────────────────────────────────────────────────────────────────────────────
private val biomassChapters = listOf(
    ChapterData(
        number   = 1,
        title    = "What is biomass energy?",
        subtitle = "Understanding organic matter as a fuel source.",
        sections = listOf(
            SectionData(
                heading = "Defining biomass",
                body    = "Biomass refers to any organic material derived from living or recently living organisms — including wood, crop residues, animal waste, and dedicated energy crops. When burned or converted, biomass releases the chemical energy originally captured from sunlight during photosynthesis.\n\nBiomass is the oldest energy source used by humanity (fire!) and today supplies about 10% of global primary energy — the largest share of any renewable source.",
                callout = "Carbon neutrality caveat: Biomass is only carbon-neutral if new plant growth absorbs the CO₂ released during combustion at the same rate. Unsustainable harvesting can make biomass a net emitter."
            ),
            SectionData(
                heading   = "Types of biomass feedstocks",
                body      = "Biomass feedstocks vary enormously in their energy content, availability, and sustainability profile.",
                flowSteps = listOf(
                    listOf("🌾 Agricultural waste", "🪵 Forestry residues", "🐄 Animal waste", "🌱 Energy crops", "⚡ Energy")
                )
            ),
            SectionData(
                heading    = "Advantages and concerns",
                body       = "Biomass offers unique flexibility compared to other renewables, but also raises important sustainability questions.",
                highlights = listOf(
                    "Dispatchable ✅"    to "Can generate on demand, unlike solar and wind",
                    "Waste reduction ✅" to "Converts agricultural and forestry waste into value",
                    "Carbon risk ⚠️"    to "Net emissions depend heavily on land use and feedstock",
                    "Land use ⚠️"      to "Energy crops can compete with food production"
                )
            )
        ),
        quiz = QuizData(
            question     = "Under what condition is biomass energy considered carbon-neutral?",
            options      = listOf(
                "When it is burned in a modern high-efficiency power plant",
                "When new plant growth absorbs CO₂ at the same rate as combustion releases it",
                "When the ash residue is buried underground permanently",
                "When biomass is mixed with coal in co-firing plants"
            ),
            correctIndex = 1
        )
    ),
    ChapterData(
        number   = 2,
        title    = "Converting biomass to energy",
        subtitle = "The technologies that turn organic matter into power.",
        sections = listOf(
            SectionData(
                heading = "Thermochemical conversion",
                body    = "Direct combustion burns biomass to produce heat, which drives steam turbines — the simplest and most widespread method. Gasification heats biomass with limited oxygen, producing syngas (CO + H₂) that can fuel turbines or be refined into liquid fuels. Pyrolysis heats in the complete absence of oxygen, yielding bio-oil, char (biochar), and gas.",
                stats   = listOf(
                    "~25–35%"  to "Combustion electrical efficiency",
                    "~40–50%"  to "Gasification combined-cycle efficiency",
                    "Biochar"  to "Stable carbon form; soil amendment",
                    "Syngas"   to "Can be upgraded to hydrogen"
                )
            ),
            SectionData(
                heading   = "Biochemical conversion",
                body      = "Anaerobic digestion uses microorganisms to break down wet organic matter (food waste, manure, sewage) in the absence of oxygen, producing biogas — mainly methane — that can generate heat and power or be injected into the gas grid. Fermentation converts sugars in crops like sugarcane or corn into bioethanol, blended with petrol to power vehicles.",
                flowSteps = listOf(
                    listOf("🐄 Organic waste", "Anaerobic digester", "🔥 Biogas (CH₄)", "⚡ Heat & Power"),
                    listOf("🌾 Sugar crops", "Fermentation", "🚗 Bioethanol", "⛽ Transport fuel")
                )
            ),
            SectionData(
                heading = "Bioenergy with CCS (BECCS)",
                body    = "BECCS combines biomass power generation with carbon capture and storage. Plants absorb CO₂ as they grow; combustion releases it; CCS captures and stores it underground. The net result is negative emissions — actively removing CO₂ from the atmosphere. BECCS is included in most climate models for staying below 1.5°C warming.",
                callout = "BECCS is one of the few technologies that can achieve negative CO₂ emissions at scale, making it a potential key tool in climate mitigation if sustainable feedstocks can be secured."
            )
        ),
        quiz = QuizData(
            question     = "What gases are produced when biomass is gasified with limited oxygen?",
            options      = listOf(
                "Oxygen and nitrogen — the same as atmospheric air",
                "Carbon dioxide and water vapour only",
                "Carbon monoxide and hydrogen (syngas)",
                "Methane and propane (natural gas mixture)"
            ),
            correctIndex = 2
        )
    ),
    ChapterData(
        number   = 3,
        title    = "Biomass in Malaysia",
        subtitle = "Palm oil, rice, and the tropical biomass opportunity.",
        sections = listOf(
            SectionData(
                heading = "Malaysia's biomass advantage",
                body    = "Malaysia is one of the world's largest producers of palm oil, rubber, and rice — industries that generate enormous quantities of organic waste. Palm oil processing alone produces empty fruit bunches (EFB), palm kernel shells (PKS), and palm oil mill effluent (POME), all of which are viable biomass feedstocks.",
                stats   = listOf(
                    "~80 million t" to "Palm biomass generated annually in Malaysia",
                    "~4,000 MW"    to "Estimated biomass power potential",
                    "POME"         to "Palm effluent → biogas via anaerobic digestion",
                    "PKS"          to "Palm kernel shells exported as pellets globally"
                )
            ),
            SectionData(
                heading = "Current usage and policies",
                body    = "Malaysia's Renewable Energy Act and the Feed-in Tariff (FiT) scheme incentivise biomass power plants. By 2023, biomass and biogas contributed about 390 MW to the national grid — small relative to potential, but growing. The New Industrial Master Plan 2030 targets greater use of biomass for green industrial energy.",
                callout = "Palm kernel shells from Malaysia are a major export commodity — shipped to South Korea and Japan as a fuel for biomass power stations under the EU and Japanese renewable energy certification schemes."
            ),
            SectionData(
                heading    = "Challenges specific to Malaysia",
                body       = "Despite vast resources, several barriers slow biomass deployment in Malaysia.",
                highlights = listOf(
                    "Collection logistics"  to "Biomass is bulky and dispersed across plantations",
                    "Moisture content"      to "Wet feedstocks require pre-drying — energy intensive",
                    "Sustainability debate" to "Palm oil expansion linked to deforestation concerns",
                    "Grid access"           to "Many mills are in remote Sabah and Sarawak"
                )
            )
        ),
        quiz = QuizData(
            question     = "Which palm oil by-product is processed via anaerobic digestion to generate biogas in Malaysia?",
            options      = listOf(
                "Palm kernel shells (PKS)",
                "Palm oil mill effluent (POME)",
                "Empty fruit bunches (EFB) when dried",
                "Refined palm olein exported for cooking"
            ),
            correctIndex = 1
        )
    ),
    ChapterData(
        number   = 4,
        title    = "Biomass & SDG 7",
        subtitle = "Bioenergy's role in sustainable development.",
        sections = listOf(
            SectionData(
                heading    = "Biomass and the SDGs",
                body       = "Biomass energy connects to several Sustainable Development Goals, particularly those relating to energy access, waste management, and climate action.",
                highlights = listOf(
                    "SDG 7.1"   to "Biogas digesters provide clean cooking energy to rural families",
                    "SDG 7.2"   to "Biomass is currently the largest renewable energy source globally",
                    "SDG 2"     to "Sustainable bioenergy must not displace food crops",
                    "SDG 12"    to "Waste-to-energy biomass promotes responsible consumption"
                )
            ),
            SectionData(
                heading = "Clean cooking and energy poverty",
                body    = "Around 2.4 billion people still cook using open fires or inefficient stoves fuelled by wood or charcoal — causing indoor air pollution responsible for ~4 million premature deaths annually. Small-scale biogas digesters fed by animal or food waste provide clean cooking gas, transforming health outcomes in rural communities in Africa, India, and Southeast Asia.",
                callout = "A simple household biogas digester fed with cattle dung can supply 2–3 hours of clean cooking gas daily — replacing dangerous wood fires and saving hours of fuel-collection labour, mostly done by women."
            ),
            SectionData(
                heading   = "Sustainable bioenergy principles",
                body      = "The sustainability of biomass is not guaranteed — it depends on what feedstock is used, how it is sourced, and what land use changes occur. The Roundtable on Sustainable Biomaterials (RSB) and similar certification schemes set standards for responsible bioenergy.",
                flowSteps = listOf(
                    listOf("🌱 Sustainable feedstock", "✅ Certified supply chain", "🏭 Efficient conversion", "⚡ Clean energy", "🌍 SDG impact")
                )
            )
        ),
        quiz = QuizData(
            question     = "How many people globally still lack access to clean cooking fuels, relying on open fires or inefficient stoves?",
            options      = listOf(
                "Around 100 million — mostly in sub-Saharan Africa",
                "Around 2.4 billion — across Asia, Africa, and Latin America",
                "Around 500 million — mainly in rural India",
                "Around 50 million — a rapidly declining problem"
            ),
            correctIndex = 1
        )
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// REGISTRY — one place to look up any topic by index
// ─────────────────────────────────────────────────────────────────────────────
val ALL_TOPICS: List<TopicMeta> = listOf(
    TopicMeta(
        index    = 0,
        emoji    = "☀️",
        title    = "Solar Energy",
        subtitle = "Photovoltaic cells & sunlight",
        iconBg   = AmberSurface,
        chapters = solarChapters
    ),
    TopicMeta(
        index    = 1,
        emoji    = "💨",
        title    = "Wind Energy",
        subtitle = "Turbines & kinetic conversion",
        iconBg   = BlueSurface,
        chapters = windChapters
    ),
    TopicMeta(
        index    = 2,
        emoji    = "💧",
        title    = "Hydropower",
        subtitle = "Dams & flowing water",
        iconBg   = GreenSurface,
        chapters = hydroChapters
    ),
    TopicMeta(
        index    = 3,
        emoji    = "🌿",
        title    = "Biomass Energy",
        subtitle = "Organic matter & biofuels",
        iconBg   = RedSurface,
        chapters = biomassChapters
    )
)
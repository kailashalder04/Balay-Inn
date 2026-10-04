package com.example.data.seed

import com.example.data.local.entity.BarangayEntity
import com.example.data.local.entity.CityMunicipalityEntity
import com.example.data.local.entity.CountryEntity
import com.example.data.local.entity.ProvinceEntity
import com.example.data.local.entity.RegionEntity
import com.example.data.local.entity.UserEntity

object MarketplaceDataSeed {

    val country = CountryEntity(
        id = "ctry_ph",
        name = "Philippines",
        code = "PH",
        isActive = true
    )

    val regionVII = RegionEntity(
        id = "reg_vii",
        countryId = "ctry_ph",
        name = "Central Visayas (Region VII)",
        code = "R07",
        isActive = true
    )

    val provinces = listOf(
        ProvinceEntity(
            id = "prov_bohol",
            regionId = "reg_vii",
            name = "Bohol",
            code = "BOH",
            isActive = true
        ),
        ProvinceEntity(
            id = "prov_cebu",
            regionId = "reg_vii",
            name = "Cebu",
            code = "CEB",
            isActive = true
        ),
        ProvinceEntity(
            id = "prov_bacolod",
            regionId = "reg_vii",
            name = "Bacolod & Negros Occidental",
            code = "BCD",
            isActive = true
        )
    )

    // Complete Bohol Administrative Coverage: 1 Component City + 47 Municipalities = 48 LGUs
    val boholMunicipalities = listOf(
        CityMunicipalityEntity("bohol_tagbilaran", "prov_bohol", "Tagbilaran City", "City", "BOH_TAG"),
        CityMunicipalityEntity("bohol_alburquerque", "prov_bohol", "Alburquerque", "Municipality", "BOH_ALB"),
        CityMunicipalityEntity("bohol_alicia", "prov_bohol", "Alicia", "Municipality", "BOH_ALI"),
        CityMunicipalityEntity("bohol_anda", "prov_bohol", "Anda", "Municipality", "BOH_AND"),
        CityMunicipalityEntity("bohol_antequera", "prov_bohol", "Antequera", "Municipality", "BOH_ANT"),
        CityMunicipalityEntity("bohol_baclayon", "prov_bohol", "Baclayon", "Municipality", "BOH_BAC"),
        CityMunicipalityEntity("bohol_balilihan", "prov_bohol", "Balilihan", "Municipality", "BOH_BAL"),
        CityMunicipalityEntity("bohol_batuan", "prov_bohol", "Batuan", "Municipality", "BOH_BAT"),
        CityMunicipalityEntity("bohol_bien_unido", "prov_bohol", "Bien Unido", "Municipality", "BOH_BIE"),
        CityMunicipalityEntity("bohol_bilar", "prov_bohol", "Bilar", "Municipality", "BOH_BIL"),
        CityMunicipalityEntity("bohol_buenavista", "prov_bohol", "Buenavista", "Municipality", "BOH_BUE"),
        CityMunicipalityEntity("bohol_calape", "prov_bohol", "Calape", "Municipality", "BOH_CAL"),
        CityMunicipalityEntity("bohol_candijay", "prov_bohol", "Candijay", "Municipality", "BOH_CAN"),
        CityMunicipalityEntity("bohol_carmen", "prov_bohol", "Carmen", "Municipality", "BOH_CAR"),
        CityMunicipalityEntity("bohol_catigbian", "prov_bohol", "Catigbian", "Municipality", "BOH_CAT"),
        CityMunicipalityEntity("bohol_clarin", "prov_bohol", "Clarin", "Municipality", "BOH_CLA"),
        CityMunicipalityEntity("bohol_corella", "prov_bohol", "Corella", "Municipality", "BOH_COR"),
        CityMunicipalityEntity("bohol_cortes", "prov_bohol", "Cortes", "Municipality", "BOH_CRT"),
        CityMunicipalityEntity("bohol_dagohoy", "prov_bohol", "Dagohoy", "Municipality", "BOH_DAG"),
        CityMunicipalityEntity("bohol_danao", "prov_bohol", "Danao", "Municipality", "BOH_DAN"),
        CityMunicipalityEntity("bohol_dauis", "prov_bohol", "Dauis", "Municipality", "BOH_DAU"),
        CityMunicipalityEntity("bohol_dimiao", "prov_bohol", "Dimiao", "Municipality", "BOH_DIM"),
        CityMunicipalityEntity("bohol_duero", "prov_bohol", "Duero", "Municipality", "BOH_DUE"),
        CityMunicipalityEntity("bohol_garcia_hernandez", "prov_bohol", "Garcia Hernandez", "Municipality", "BOH_GAR"),
        CityMunicipalityEntity("bohol_getafe", "prov_bohol", "Getafe", "Municipality", "BOH_GET"),
        CityMunicipalityEntity("bohol_guindulman", "prov_bohol", "Guindulman", "Municipality", "BOH_GUI"),
        CityMunicipalityEntity("bohol_inabanga", "prov_bohol", "Inabanga", "Municipality", "BOH_INA"),
        CityMunicipalityEntity("bohol_jagna", "prov_bohol", "Jagna", "Municipality", "BOH_JAG"),
        CityMunicipalityEntity("bohol_lila", "prov_bohol", "Lila", "Municipality", "BOH_LIL"),
        CityMunicipalityEntity("bohol_loay", "prov_bohol", "Loay", "Municipality", "BOH_LOA"),
        CityMunicipalityEntity("bohol_loboc", "prov_bohol", "Loboc", "Municipality", "BOH_LOB"),
        CityMunicipalityEntity("bohol_loon", "prov_bohol", "Loon", "Municipality", "BOH_LOO"),
        CityMunicipalityEntity("bohol_mabini", "prov_bohol", "Mabini", "Municipality", "BOH_MAB"),
        CityMunicipalityEntity("bohol_maribojoc", "prov_bohol", "Maribojoc", "Municipality", "BOH_MAR"),
        CityMunicipalityEntity("bohol_panglao", "prov_bohol", "Panglao", "Municipality", "BOH_PAN"),
        CityMunicipalityEntity("bohol_pilar", "prov_bohol", "Pilar", "Municipality", "BOH_PIL"),
        CityMunicipalityEntity("bohol_pres_carlos_garcia", "prov_bohol", "Pres. Carlos P. Garcia", "Municipality", "BOH_CPG"),
        CityMunicipalityEntity("bohol_sagbayan", "prov_bohol", "Sagbayan", "Municipality", "BOH_SAG"),
        CityMunicipalityEntity("bohol_san_isidro", "prov_bohol", "San Isidro", "Municipality", "BOH_SIS"),
        CityMunicipalityEntity("bohol_san_miguel", "prov_bohol", "San Miguel", "Municipality", "BOH_SMI"),
        CityMunicipalityEntity("bohol_sevilla", "prov_bohol", "Sevilla", "Municipality", "BOH_SEV"),
        CityMunicipalityEntity("bohol_sierra_bullones", "prov_bohol", "Sierra Bullones", "Municipality", "BOH_SBU"),
        CityMunicipalityEntity("bohol_sikatuna", "prov_bohol", "Sikatuna", "Municipality", "BOH_SIK"),
        CityMunicipalityEntity("bohol_talibon", "prov_bohol", "Talibon", "Municipality", "BOH_TAL"),
        CityMunicipalityEntity("bohol_trinidad", "prov_bohol", "Trinidad", "Municipality", "BOH_TRI"),
        CityMunicipalityEntity("bohol_tubigon", "prov_bohol", "Tubigon", "Municipality", "BOH_TUB"),
        CityMunicipalityEntity("bohol_ubay", "prov_bohol", "Ubay", "Municipality", "BOH_UBA"),
        CityMunicipalityEntity("bohol_valencia", "prov_bohol", "Valencia", "Municipality", "BOH_VAL")
    )

    // Cebu Major Cities & Municipalities
    val cebuCities = listOf(
        CityMunicipalityEntity("cebu_cebu_city", "prov_cebu", "Cebu City", "City", "CEB_CEB"),
        CityMunicipalityEntity("cebu_mandaue_city", "prov_cebu", "Mandaue City", "City", "CEB_MAN"),
        CityMunicipalityEntity("cebu_lapu_lapu_city", "prov_cebu", "Lapu-Lapu City", "City", "CEB_LAP"),
        CityMunicipalityEntity("cebu_talisay_city", "prov_cebu", "Talisay City", "City", "CEB_TAL"),
        CityMunicipalityEntity("cebu_consolacion", "prov_cebu", "Consolacion", "Municipality", "CEB_CON"),
        CityMunicipalityEntity("cebu_liloan", "prov_cebu", "Liloan", "Municipality", "CEB_LIL")
    )

    // Bacolod & Negros Occidental Cities and Municipalities
    val bacolodCities = listOf(
        // Highly Urbanized Provincial Capital
        CityMunicipalityEntity("bacolod_city", "prov_bacolod", "Bacolod City", "City", "BCD_BAC"),

        // Metro Bacolod & Adjacent Component Cities
        CityMunicipalityEntity("bacolod_talisay", "prov_bacolod", "Talisay City", "City", "BCD_TAL"),
        CityMunicipalityEntity("bacolod_silay", "prov_bacolod", "Silay City", "City", "BCD_SIL"),
        CityMunicipalityEntity("bacolod_bago", "prov_bacolod", "Bago City", "City", "BCD_BAG"),
        CityMunicipalityEntity("bacolod_victorias", "prov_bacolod", "Victorias City", "City", "BCD_VIC"),
        CityMunicipalityEntity("bacolod_cadiz", "prov_bacolod", "Cadiz City", "City", "BCD_CAD"),
        CityMunicipalityEntity("bacolod_sagay", "prov_bacolod", "Sagay City", "City", "BCD_SAG"),
        CityMunicipalityEntity("bacolod_san_carlos", "prov_bacolod", "San Carlos City", "City", "BCD_SCA"),
        CityMunicipalityEntity("bacolod_kabankalan", "prov_bacolod", "Kabankalan City", "City", "BCD_KAB"),
        CityMunicipalityEntity("bacolod_himamaylan", "prov_bacolod", "Himamaylan City", "City", "BCD_HIM"),
        CityMunicipalityEntity("bacolod_la_carlota", "prov_bacolod", "La Carlota City", "City", "BCD_LCA"),
        CityMunicipalityEntity("bacolod_escalante", "prov_bacolod", "Escalante City", "City", "BCD_ESC"),
        CityMunicipalityEntity("bacolod_sipalay", "prov_bacolod", "Sipalay City", "City", "BCD_SIP"),

        // Municipalities of Bacolod / Negros Occidental
        CityMunicipalityEntity("bacolod_murcia", "prov_bacolod", "Murcia", "Municipality", "BCD_MUR"),
        CityMunicipalityEntity("bacolod_eb_magalona", "prov_bacolod", "E.B. Magalona", "Municipality", "BCD_EBM"),
        CityMunicipalityEntity("bacolod_hinigaran", "prov_bacolod", "Hinigaran", "Municipality", "BCD_HIN"),
        CityMunicipalityEntity("bacolod_binalbagan", "prov_bacolod", "Binalbagan", "Municipality", "BCD_BIN"),
        CityMunicipalityEntity("bacolod_manapla", "prov_bacolod", "Manapla", "Municipality", "BCD_MAN"),
        CityMunicipalityEntity("bacolod_pulupandan", "prov_bacolod", "Pulupandan", "Municipality", "BCD_PUL"),
        CityMunicipalityEntity("bacolod_valladolid", "prov_bacolod", "Valladolid", "Municipality", "BCD_VAL"),
        CityMunicipalityEntity("bacolod_san_enrique", "prov_bacolod", "San Enrique", "Municipality", "BCD_SEN"),
        CityMunicipalityEntity("bacolod_pontevedra", "prov_bacolod", "Pontevedra", "Municipality", "BCD_PON"),
        CityMunicipalityEntity("bacolod_dsb", "prov_bacolod", "Don Salvador Benedicto", "Municipality", "BCD_DSB"),
        CityMunicipalityEntity("bacolod_la_castellana", "prov_bacolod", "La Castellana", "Municipality", "BCD_LCS"),
        CityMunicipalityEntity("bacolod_moises_padilla", "prov_bacolod", "Moises Padilla", "Municipality", "BCD_MPA"),
        CityMunicipalityEntity("bacolod_isabela", "prov_bacolod", "Isabela", "Municipality", "BCD_ISA"),
        CityMunicipalityEntity("bacolod_cauayan", "prov_bacolod", "Cauayan", "Municipality", "BCD_CAU"),
        CityMunicipalityEntity("bacolod_ilog", "prov_bacolod", "Ilog", "Municipality", "BCD_ILO"),
        CityMunicipalityEntity("bacolod_candoni", "prov_bacolod", "Candoni", "Municipality", "BCD_CAN"),
        CityMunicipalityEntity("bacolod_hinobaan", "prov_bacolod", "Hinoba-an", "Municipality", "BCD_HNB"),
        CityMunicipalityEntity("bacolod_toboso", "prov_bacolod", "Toboso", "Municipality", "BCD_TOB"),
        CityMunicipalityEntity("bacolod_calatrava", "prov_bacolod", "Calatrava", "Municipality", "BCD_CAL")
    )

    fun generateBarangays(): List<BarangayEntity> {
        val list = mutableListOf<BarangayEntity>()

        // 1. Tagbilaran City Barangays
        val tagbilaranBgy = listOf("Poblacion I", "Poblacion II", "Poblacion III", "Bool", "Booy", "Cabawan", "Cogon", "Dampas", "Dao", "Manga", "Mansasa", "San Isidro", "Taloto", "Tiptip", "Ubujan")
        tagbilaranBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_tag_$i", "bohol_tagbilaran", name, "BOH_TAG_$i"))
        }

        // 2. Panglao Barangays
        val panglaoBgy = listOf("Poblacion", "Bil-isan", "Bolod", "Danao", "Doljo", "Libaong", "Looc", "Madrangca", "Tangnan", "Tawala")
        panglaoBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_pan_$i", "bohol_panglao", name, "BOH_PAN_$i"))
        }

        // 3. Dauis Barangays
        val dauisBgy = listOf("Poblacion", "Biking", "Bingag", "Catarman", "Dao", "Mariveles", "Mayacabac", "Songculan", "Tabalong", "Tinago")
        dauisBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_dau_$i", "bohol_dauis", name, "BOH_DAU_$i"))
        }

        // 4. Baclayon Barangays
        val baclayonBgy = listOf("Poblacion", "Cambacay", "Guiwanon", "Landican", "Laya", "Payahan", "San Roque", "Santa Cruz", "Taguihon")
        baclayonBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bac_$i", "bohol_baclayon", name, "BOH_BAC_$i"))
        }

        // 5. Loboc Barangays
        val lobocBgy = listOf("Poblacion Sawang", "Poblacion Ondol", "Bagumbayan", "Bahian", "Bonbon", "Calunasan", "Gotozon", "Jimilian", "Oy", "Valladolid")
        lobocBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_lob_$i", "bohol_loboc", name, "BOH_LOB_$i"))
        }

        // 6. Carmen Barangays
        val carmenBgy = listOf("Poblacion Sur", "Poblacion Norte", "Alegria", "Bicao", "Buenos Aires", "Guadalupe", "Katipunan", "La Libertad", "Montesuerte", "Nueva Vida")
        carmenBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_car_$i", "bohol_carmen", name, "BOH_CAR_$i"))
        }

        // 7. Tubigon Barangays
        val tubigonBgy = listOf("Centro", "Pooc Occidental", "Pooc Oriental", "Cabulijan", "Cahayag", "Ilijan Norte", "Macaas", "Panadtaran", "Pinayagan Norte", "Tinangnan")
        tubigonBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_tub_$i", "bohol_tubigon", name, "BOH_TUB_$i"))
        }

        // 8. Jagna Barangays
        val jagnaBgy = listOf("Poblacion", "Can-upao", "Looc", "Tejero", "Bunga Ilaya", "Cantagay", "Ipil", "Malbog", "Mayana", "Pangdan")
        jagnaBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_jag_$i", "bohol_jagna", name, "BOH_JAG_$i"))
        }

        // 9. Anda Barangays
        val andaBgy = listOf("Poblacion", "Almaria", "Bacong", "Badiang", "Buenasuerte", "Candabong", "Casica", "Katipunan", "Linawan", "Lundag", "Suba", "Talisay")
        andaBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_and_$i", "bohol_anda", name, "BOH_AND_$i"))
        }

        // 10. Ubay Barangays
        val ubayBgy = listOf("Poblacion", "Fatima", "Casate", "Bood", "Camambugan", "Humayhumay", "San Pascual", "Tapal", "Tubog")
        ubayBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_uba_$i", "bohol_ubay", name, "BOH_UBA_$i"))
        }

        // 11. Talibon Barangays
        val talibonBgy = listOf("Poblacion", "San Jose", "San Pedro", "San Roque", "Santo Niño", "Burgos", "Guio-ang", "Tanghaligue")
        talibonBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_tal_$i", "bohol_talibon", name, "BOH_TAL_$i"))
        }

        // 12. Inabanga Barangays
        val inabangaBgy = listOf("Poblacion", "Cawayan", "Dagohoy", "Dagnawan", "Lawis", "Liloan", "Nabuad", "Saa", "Tungod")
        inabangaBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_ina_$i", "bohol_inabanga", name, "BOH_INA_$i"))
        }

        // Remaining Bohol Municipalities - Seed authentic primary Poblacion & neighboring barangays
        val remainingBohol = boholMunicipalities.filter { m ->
            m.id !in listOf("bohol_tagbilaran", "bohol_panglao", "bohol_dauis", "bohol_baclayon", "bohol_loboc", "bohol_carmen", "bohol_tubigon", "bohol_jagna", "bohol_anda", "bohol_ubay", "bohol_talibon", "bohol_inabanga")
        }
        remainingBohol.forEach { m ->
            list.add(BarangayEntity("bgy_${m.code.lowercase()}_pob", m.id, "Poblacion", "${m.code}_POB"))
            list.add(BarangayEntity("bgy_${m.code.lowercase()}_san_roque", m.id, "San Roque", "${m.code}_SRO"))
            list.add(BarangayEntity("bgy_${m.code.lowercase()}_santa_cruz", m.id, "Santa Cruz", "${m.code}_STC"))
            list.add(BarangayEntity("bgy_${m.code.lowercase()}_san_isidro", m.id, "San Isidro", "${m.code}_SIS"))
        }

        // Cebu City Barangays (Lahug, Banilad, Mabolo, Guadalupe, Talamban, Apas, Capitol Site, etc.)
        val cebuCityBgy = listOf(
            "Lahug", "Banilad", "Mabolo", "Guadalupe", "Talamban", "Apas (Cebu IT Park)",
            "Capitol Site", "Fuente Osmeña", "Kasambagan", "Luz", "Kamputhaw", "Punta Princesa",
            "Labangon", "Mambaling", "San Nicolas", "Colon / Downtown", "Busay", "Mabini"
        )
        cebuCityBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_ceb_c_$i", "cebu_cebu_city", name, "CEB_C_$i"))
        }

        // Mandaue City Barangays
        val mandaueBgy = listOf("Bakilid", "Banilad", "Centro", "Guizo", "Tipolo", "Subangdaku", "Maguikay", "Cabancalan", "Basak", "Alang-alang")
        mandaueBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_ceb_m_$i", "cebu_mandaue_city", name, "CEB_M_$i"))
        }

        // Lapu-Lapu City Barangays
        val lapuLapuBgy = listOf("Poblacion", "Maribago", "Mactan", "Basak", "Marigondon", "Buaya", "Bankal", "Agus", "Subabasbas")
        lapuLapuBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_ceb_l_$i", "cebu_lapu_lapu_city", name, "CEB_L_$i"))
        }

        // Talisay City Barangays
        val talisayBgy = listOf("Poblacion", "Bulacao", "Dumlog", "San Roque", "Tabunok", "Pooc", "Cansojong")
        talisayBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_ceb_t_$i", "cebu_talisay_city", name, "CEB_T_$i"))
        }

        // Bacolod City Barangays (Mandalagan, Villamonte, Bata, Taculing, Singcang, Mansilingan, etc.)
        val bacolodBgy = listOf(
            "Mandalagan", "Villamonte (USLS Area)", "Bata", "Taculing", "Singcang-Airport",
            "Mansilingan", "Alijis", "Tangub", "Estefania", "Banago",
            "Granada", "Sum-ag", "Downtown (Barangay 1)", "Handumanan",
            "Pahanocoy", "Punta Taytay", "Montevista", "Barangay 16", "Barangay 29"
        )
        bacolodBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_$i", "bacolod_city", name, "BCD_$i"))
        }

        // Talisay City (Negros Occidental) Barangays
        val talisayNegrosBgy = listOf("Zone 1 (Poblacion)", "Zone 2", "Zone 3", "Zone 4", "Zone 5", "Zone 6", "Zone 7", "Zone 8", "Zone 9", "Zone 10", "Zone 11", "Zone 12", "Zone 14", "Zone 15", "Bubog", "Cabatangan", "Concepcion", "Dos Hermanas", "Efigenio Lizares", "Matab-ang", "San Fernando")
        talisayNegrosBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_tal_$i", "bacolod_talisay", name, "BCD_TAL_$i"))
        }

        // Silay City Barangays
        val silayBgy = listOf("Barangay I (Poblacion)", "Barangay II", "Barangay III", "Barangay IV", "Barangay V", "Bagtic", "Balaring", "E. Lopez", "Guinhalaran", "Kapitan Ramon", "Lantad", "Mambulac", "Patag", "Rizal")
        silayBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_sil_$i", "bacolod_silay", name, "BCD_SIL_$i"))
        }

        // Bago City Barangays
        val bagoBgy = listOf("Poblacion", "Balingasag", "Busay", "Caridad", "Dulao", "Ilijan", "Lag-asan", "Ma-ao", "Mailum", "Malingin", "Napoles", "Pacol", "Sampinit", "Taloc")
        bagoBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_bag_$i", "bacolod_bago", name, "BCD_BAG_$i"))
        }

        // Victorias City Barangays
        val victoriasBgy = listOf("Barangay I", "Barangay II", "Barangay III", "Barangay IV", "Barangay V", "Barangay VI", "Barangay VII", "Estado", "Gahit", "Salvacion", "Daan Banwa")
        victoriasBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_vic_$i", "bacolod_victorias", name, "BCD_VIC_$i"))
        }

        // Murcia Barangays
        val murciaBgy = listOf("Poblacion", "Zone 1", "Zone 2", "Zone 3", "Alegria", "Blumentritt", "Buenavista", "Caliban", "Canlandog", "Cansilayan", "Damsite", "Lopez Jaena", "Minoyan (Mambukal)", "Salvacion", "Santa Cruz")
        murciaBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_mur_$i", "bacolod_murcia", name, "BCD_MUR_$i"))
        }

        // Kabankalan City Barangays
        val kabankalanBgy = listOf("Barangay 1 (Poblacion)", "Barangay 2", "Barangay 3", "Barangay 4", "Barangay 5", "Barangay 6", "Barangay 7", "Barangay 8", "Barangay 9", "Camansi", "Camingawan", "Carol-an", "Daan Banwa", "Hilamonan", "Inapoy", "Linao", "Magballo", "Oringao", "Orong", "Salong", "Tabugon", "Talubangi", "Tampalon")
        kabankalanBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_kab_$i", "bacolod_kabankalan", name, "BCD_KAB_$i"))
        }

        // San Carlos City Barangays
        val sanCarlosBgy = listOf("Barangay 1", "Barangay 2", "Barangay 3", "Barangay 4", "Barangay 5", "Barangay 6", "Bagonbon", "Buluangan", "Codcod", "Ermita", "Guadalupe", "Nataban", "Palampas", "Prosperidad", "Punao", "Quezon", "Rizal", "San Juan")
        sanCarlosBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_sca_$i", "bacolod_san_carlos", name, "BCD_SCA_$i"))
        }

        // Cadiz City Barangays
        val cadizBgy = listOf("Poblacion 1", "Poblacion 2", "Poblacion 3", "Banquerohan", "Cabahug", "Cadiz Viejo", "Celestino Villacin", "Daga", "Jerusalem", "Luna", "Mabini", "Sicaba", "Tiglawigan", "Tinampa-an", "V.F. Gustilo")
        cadizBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_cad_$i", "bacolod_cadiz", name, "BCD_CAD_$i"))
        }

        // Sagay City Barangays
        val sagayBgy = listOf("Poblacion 1", "Poblacion 2", "Bato", "Bulanon", "Campo Himoga-an", "Colonia Divina", "Fabrica", "General Luna", "Himoga-an Baybay", "Lopez Jaena", "Old Sagay", "Paraiso", "Plaridel", "Puey", "Rizal", "Taba-ao", "Vito")
        sagayBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_sag_$i", "bacolod_sagay", name, "BCD_SAG_$i"))
        }

        // Himamaylan City Barangays
        val himamaylanBgy = listOf("Poblacion", "Aguisan", "Buenavista", "Cabadiangan", "Cabanbanan", "Carabalan", "Caradio-an", "Libacao", "Mambagaton", "Nabali-an", "San Antonio", "Sara-et", "Su-ay", "Talaban", "To-oy")
        himamaylanBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_him_$i", "bacolod_himamaylan", name, "BCD_HIM_$i"))
        }

        // La Carlota City Barangays
        val laCarlotaBgy = listOf("Barangay 1", "Barangay 2", "Barangay 3", "Ara-al", "Ayungon", "Balabag", "Batuan", "Cubay", "Haguimit", "La Granja", "Nagasi", "San Miguel", "Ybalon")
        laCarlotaBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_lca_$i", "bacolod_la_carlota", name, "BCD_LCA_$i"))
        }

        // Escalante City Barangays
        val escalanteBgy = listOf("Poblacion", "Balintawak", "Cervantes", "Dian-ay", "Hacienda Fe", "Jonobjonob", "Langub", "Libertad", "Mabini", "Malbug", "Old Escalante", "Paitan", "Pinapugasan", "Rizal", "Tamlang", "Udtongan", "Washington")
        escalanteBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_esc_$i", "bacolod_escalante", name, "BCD_ESC_$i"))
        }

        // Sipalay City Barangays
        val sipalayBgy = listOf("Poblacion", "Camparic", "Cartagena", "Cayhagan", "Gil Montilla", "Mambaroto", "Manlocahoc", "Maricalum", "Nabulao", "Nauhang", "San Jose")
        sipalayBgy.forEachIndexed { i, name ->
            list.add(BarangayEntity("bgy_bcd_sip_$i", "bacolod_sipalay", name, "BCD_SIP_$i"))
        }

        // Remaining Bacolod / Negros Municipalities
        val remainingBacolodMunicipalities = bacolodCities.filter { c ->
            c.id !in listOf(
                "bacolod_city", "bacolod_talisay", "bacolod_silay", "bacolod_bago",
                "bacolod_victorias", "bacolod_murcia", "bacolod_kabankalan", "bacolod_san_carlos",
                "bacolod_cadiz", "bacolod_sagay", "bacolod_himamaylan", "bacolod_la_carlota",
                "bacolod_escalante", "bacolod_sipalay"
            )
        }
        remainingBacolodMunicipalities.forEach { m ->
            list.add(BarangayEntity("bgy_${m.code.lowercase()}_pob", m.id, "Poblacion", "${m.code}_POB"))
            list.add(BarangayEntity("bgy_${m.code.lowercase()}_san_roque", m.id, "San Roque", "${m.code}_SRO"))
            list.add(BarangayEntity("bgy_${m.code.lowercase()}_san_isidro", m.id, "San Isidro", "${m.code}_SIS"))
            list.add(BarangayEntity("bgy_${m.code.lowercase()}_santa_cruz", m.id, "Santa Cruz", "${m.code}_STC"))
            list.add(BarangayEntity("bgy_${m.code.lowercase()}_central", m.id, "Central", "${m.code}_CEN"))
        }

        return list
    }

    val seedUsers = listOf(
        UserEntity(
            id = "usr_tenant_1",
            fullName = "Joshua Alcantara",
            email = "joshua.alcantara@gmail.com",
            phone = "+63 917 555 0192",
            role = "TENANT",
            relationshipToProperty = "Tenant",
            verificationLevel = 2,
            phoneVerified = true,
            emailVerified = true
        ),
        UserEntity(
            id = "usr_tenant_2",
            fullName = "Andrea Lim",
            email = "andrea.lim@yahoo.com",
            phone = "+63 928 444 8821",
            role = "TENANT",
            relationshipToProperty = "Tenant",
            verificationLevel = 3,
            phoneVerified = true,
            emailVerified = true,
            identitySubmitted = true
        ),
        UserEntity(
            id = "usr_owner_1",
            fullName = "Donato Yap",
            email = "donato.yap@boholrentals.ph",
            phone = "+63 918 901 2345",
            role = "PROPERTY_OWNER",
            relationshipToProperty = "Property owner",
            isAuthorizedByOwner = true,
            verificationLevel = 4,
            phoneVerified = true,
            emailVerified = true,
            identitySubmitted = true
        ),
        UserEntity(
            id = "usr_rep_family",
            fullName = "Maria Santos",
            email = "maria.santos@gmail.com",
            phone = "+63 920 123 7890",
            role = "FAMILY_MEMBER",
            relationshipToProperty = "Family member",
            isAuthorizedByOwner = true,
            verificationLevel = 2,
            phoneVerified = true,
            emailVerified = true
        ),
        UserEntity(
            id = "usr_rep_caretaker",
            fullName = "Pedro Flores",
            email = "pedro.caretaker@gmail.com",
            phone = "+63 939 678 1234",
            role = "CARETAKER",
            relationshipToProperty = "Caretaker",
            isAuthorizedByOwner = true,
            verificationLevel = 2,
            phoneVerified = true,
            emailVerified = true
        ),
        UserEntity(
            id = "usr_broker_marco",
            fullName = "Marco Villanueva, REB",
            email = "marco.villanueva@visayasrealty.ph",
            phone = "+63 917 888 9911",
            role = "REAL_ESTATE_BROKER",
            relationshipToProperty = "Licensed broker",
            isAuthorizedByOwner = true,
            verificationLevel = 5,
            phoneVerified = true,
            emailVerified = true,
            identitySubmitted = true,
            professionalVerified = true,
            prcNumber = "PRC-REB-0028192"
        ),
        UserEntity(
            id = "usr_admin_1",
            fullName = "Balay-Inn Compliance Admin",
            email = "admin@balayinn.ph",
            phone = "+63 905 111 2233",
            role = "ADMIN",
            relationshipToProperty = "Platform Staff",
            verificationLevel = 5,
            phoneVerified = true,
            emailVerified = true
        )
    )
}

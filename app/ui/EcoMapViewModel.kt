package my.com.a221491_amiraizatbinharith_nelson_project2.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ─── UI State ─────────────────────────────────────────────────────────────────
data class EcoMapUiState(
    val activeFilter  : EnergyType              = EnergyType.ALL,
    val selectedSite  : MalaysianEnergySite?    = null,
    val showSdg7Sheet : Boolean                 = false,
    val filteredSites : List<MalaysianEnergySite> = MALAYSIA_ENERGY_SITES
)

// ─── ViewModel ────────────────────────────────────────────────────────────────
class EcoMapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EcoMapUiState())
    val uiState: StateFlow<EcoMapUiState> = _uiState.asStateFlow()

    // ── Filter ────────────────────────────────────────────────────────────────
    fun setFilter(type: EnergyType) {
        _uiState.update { current ->
            val filtered = if (type == EnergyType.ALL) MALAYSIA_ENERGY_SITES
                           else MALAYSIA_ENERGY_SITES.filter { it.type == type }
            current.copy(activeFilter = type, filteredSites = filtered)
        }
    }

    // ── Site selection ────────────────────────────────────────────────────────
    fun selectSite(site: MalaysianEnergySite) {
        _uiState.update { it.copy(selectedSite = site) }
    }

    fun clearSelectedSite() {
        _uiState.update { it.copy(selectedSite = null) }
    }

    // ── SDG 7 sheet ───────────────────────────────────────────────────────────
    fun showSdg7Sheet() {
        _uiState.update { it.copy(showSdg7Sheet = true) }
    }

    fun hideSdg7Sheet() {
        _uiState.update { it.copy(showSdg7Sheet = false) }
    }
}

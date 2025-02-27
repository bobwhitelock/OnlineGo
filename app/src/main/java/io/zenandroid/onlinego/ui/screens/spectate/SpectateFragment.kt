package io.zenandroid.onlinego.ui.screens.spectate

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import io.zenandroid.onlinego.OnlineGoApplication
import io.zenandroid.onlinego.R
import io.zenandroid.onlinego.ui.screens.main.MainActivity
import io.zenandroid.onlinego.data.model.local.Game
import io.zenandroid.onlinego.data.model.ogs.GameData
import io.zenandroid.onlinego.data.model.ogs.GameList
import io.zenandroid.onlinego.data.model.ogs.OGSGame
import io.zenandroid.onlinego.data.ogs.Move
import io.zenandroid.onlinego.ui.screens.game.GAME_ID
import org.koin.android.ext.android.get

/**
 * Created by alex on 05/11/2017.
 */
@Deprecated("Obsolete")
class SpectateFragment : Fragment(), SpectateContract.View {

    private lateinit var presenter: SpectateContract.Presenter
    private val spectateAdapter = SpectateAdapter()
    private val analytics = OnlineGoApplication.instance.analytics

    override var games: GameList? = null
        set(value) {
            field = value
            value?.let {
                spectateAdapter.setGames(it.results)
                spectateAdapter.clicks.subscribe(presenter::onGameSelected)
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_spectate, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<RecyclerView>(R.id.gamesRecycler).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = spectateAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        presenter = SpectatePresenter(this, get())
    }

    override fun navigateToGameScreen(game: OGSGame) {
        analytics.logEvent("spectate_game", Bundle().apply { putLong("GAME_ID", game.id) })
        (requireActivity() as MainActivity).navigateToGameScreen(Game.fromOGSGame(game))
    }

    override fun onResume() {
        super.onResume()
        analytics.setCurrentScreen(requireActivity(), javaClass.simpleName, null)
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    override fun setGameData(id: Long, gameData: GameData) {
        spectateAdapter.setGameData(id, gameData)
    }

    override fun doMove(id: Long, move: Move) {
        spectateAdapter.doMove(id, move)
    }

}
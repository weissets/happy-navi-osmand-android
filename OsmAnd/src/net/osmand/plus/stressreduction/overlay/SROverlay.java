package net.osmand.plus.stressreduction.overlay;

import net.osmand.data.RotatedTileBox;
import net.osmand.plus.views.OsmandMapLayer;
import net.osmand.plus.views.OsmandMapTileView;

import android.graphics.Canvas;

import java.util.Arrays;

/**
 * Created by Tobias on 05/09/15.
 */
public class SROverlay extends OsmandMapLayer {

	@Override
	public void initLayer(OsmandMapTileView view) {
		view.addLayer(this, 0.85f);
	}

	@Override
	public void onDraw(Canvas canvas, RotatedTileBox tileBox, DrawSettings settings) {
		tileBox.getTileBounds();
	}

	@Override
	public void destroyLayer() {

	}

	/**
	 * This method returns whether canvas should be rotated as
	 * map rotated before {@link #onDraw(Canvas, RotatedTileBox, DrawSettings)}.
	 * If the layer draws simply layer over screen (not over map)
	 * it should return true.
	 */
	@Override
	public boolean drawInScreenPixels() {
		return false;
	}
}

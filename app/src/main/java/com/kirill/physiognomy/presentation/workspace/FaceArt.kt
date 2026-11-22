package com.kirill.physiognomy.presentation.workspace

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun FaceArt(
    type: String, // "convex", "sloping", "straight", "brow_ridge", "flat", "will_bump"
    color: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(width = 100.dp, height = 130.dp)) {
        val w = size.width
        val h = size.height

        // Масштабируем координаты из SVG (200x260) под наш Canvas
        val scaleX = w / 200f
        val scaleY = h / 260f

        fun p(x: Float, y: Float) = Offset(x * scaleX, y * scaleY)

        val strokeWidth = 2f * scaleX

        // Общий контур лица (упрощенный для всех)
        val facePath = Path().apply {
            moveTo(p(120f, 245f).x, p(120f, 245f).y)
            quadraticTo(p(130f, 230f).x, p(130f, 230f).y, p(140f, 210f).x, p(140f, 210f).y)
            quadraticTo(p(145f, 200f).x, p(145f, 200f).y, p(148f, 190f).x, p(148f, 190f).y)
            quadraticTo(p(155f, 180f).x, p(155f, 180f).y, p(158f, 170f).x, p(158f, 170f).y)
            quadraticTo(p(162f, 155f).x, p(162f, 155f).y, p(158f, 150f).x, p(158f, 150f).y)
            quadraticTo(p(155f, 140f).x, p(155f, 140f).y, p(160f, 130f).x, p(160f, 130f).y)
            // Волосы
            moveTo(p(140f, 90f).x, p(140f, 90f).y)
            quadraticTo(p(148f, 70f).x, p(148f, 70f).y, p(145f, 50f).x, p(145f, 50f).y)
            quadraticTo(p(140f, 15f).x, p(140f, 15f).y, p(100f, 15f).x, p(100f, 15f).y)
            quadraticTo(p(60f, 20f).x, p(60f, 20f).y, p(55f, 55f).x, p(55f, 55f).y)
        }
        drawPath(facePath, color = color.copy(alpha = 0.3f), style = Stroke(strokeWidth))

        // Рисуем акцент в зависимости от типа
        val accentPath = Path()
        when (type) {
            "convex" -> { // Крутой
                accentPath.moveTo(p(140f, 90f).x, p(140f, 90f).y)
                accentPath.quadraticTo(p(160f, 85f).x, p(160f, 85f).y, p(165f, 100f).x, p(165f, 100f).y)
                accentPath.quadraticTo(p(168f, 115f).x, p(168f, 115f).y, p(160f, 130f).x, p(160f, 130f).y)
            }
            "sloping" -> { // Покатый
                accentPath.moveTo(p(145f, 85f).x, p(145f, 85f).y)
                accentPath.quadraticTo(p(130f, 95f).x, p(130f, 95f).y, p(120f, 110f).x, p(120f, 110f).y)
                accentPath.quadraticTo(p(115f, 120f).x, p(115f, 120f).y, p(118f, 130f).x, p(118f, 130f).y)
            }
            "straight" -> { // Прямой
                accentPath.moveTo(p(142f, 85f).x, p(142f, 85f).y)
                accentPath.quadraticTo(p(143f, 100f).x, p(143f, 100f).y, p(142f, 115f).x, p(142f, 115f).y)
                accentPath.quadraticTo(p(141f, 125f).x, p(141f, 125f).y, p(140f, 130f).x, p(140f, 130f).y)
            }
            "brow_ridge" -> { // Надбровные дуги
                accentPath.moveTo(p(140f, 130f).x, p(140f, 130f).y)
                accentPath.quadraticTo(p(155f, 128f).x, p(155f, 128f).y, p(162f, 132f).x, p(162f, 132f).y)
                accentPath.quadraticTo(p(165f, 135f).x, p(165f, 135f).y, p(160f, 140f).x, p(160f, 140f).y)
                accentPath.quadraticTo(p(155f, 142f).x, p(155f, 142f).y, p(148f, 140f).x, p(148f, 140f).y)
            }
            "flat" -> { // Плоский
                accentPath.moveTo(p(140f, 130f).x, p(140f, 130f).y)
                accentPath.quadraticTo(p(142f, 135f).x, p(142f, 135f).y, p(143f, 140f).x, p(143f, 140f).y)
                accentPath.quadraticTo(p(145f, 145f).x, p(145f, 145f).y, p(143f, 150f).x, p(143f, 150f).y)
            }
            "will_bump" -> { // Волевая шишка
                accentPath.moveTo(p(140f, 130f).x, p(140f, 130f).y)
                accentPath.quadraticTo(p(150f, 128f).x, p(150f, 128f).y, p(155f, 132f).x, p(155f, 132f).y)
                accentPath.quadraticTo(p(158f, 136f).x, p(158f, 136f).y, p(155f, 140f).x, p(155f, 140f).y)
                accentPath.quadraticTo(p(152f, 143f).x, p(152f, 143f).y, p(148f, 142f).x, p(148f, 142f).y)
            }
        }

        // Рисуем выделенную линию (акцент)
        drawPath(accentPath, color = accentColor, style = Stroke(width = 4f * scaleX, cap = StrokeCap.Round))

        // Для волевой шишки добавим кружок
        if (type == "will_bump") {
            drawCircle(
                color = accentColor,
                radius = 6f * scaleX,
                center = p(153f, 135f),
                style = Stroke(width = 2f * scaleX)
            )
        }
    }
}
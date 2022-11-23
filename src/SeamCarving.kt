import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.*

class SeamCarving {

    data class PixelData(
        val cord: Pair<Int, Int> = Pair(0, 0),
        val energy: Double = 0.0,
        var vEnergy: Double = 0.0,
        val hEnergy: Double = 0.0
    )

    fun grad2(axis: String, x: Int, y: Int, image: BufferedImage): Double {
        val pixel1 = Color(image.getRGB(if (axis == "x") x - 1 else x, if (axis == "y") y - 1 else y))
        val pixel2 = Color(image.getRGB(if (axis == "x") x + 1 else x, if (axis == "y") y + 1 else y))
        val rDiff = pixel2.red - pixel1.red
        val gDiff = pixel2.green - pixel1.green
        val bDiff = pixel2.blue - pixel1.blue
        return (rDiff * rDiff + gDiff * gDiff + bDiff * bDiff).toDouble()
    }

    fun energy(x: Int, y: Int, im: BufferedImage): Double {
        return sqrt(grad2("x", x.coerceIn(1, im.width - 2), y, im) + grad2("y", x, y.coerceIn(1, im.height - 2), im))
    }

    fun createEnergyMatrix(originalImage: BufferedImage): List<MutableList<PixelData>> {
        val energyMatrix = List(originalImage.width) { MutableList(originalImage.height) { PixelData() } }
        for (x in 0 until originalImage.width) {
            for (y in 0 until originalImage.height) {
                val energy = energy(x, y, originalImage)
                val hEnergy = if (x == 0) energy else (List(3) {
                    energyMatrix[x - 1][(y - 1 + it).coerceIn(
                        0,
                        energyMatrix[0].lastIndex
                    )].hEnergy
                }.minOrNull() ?: 0.0) + energy
                energyMatrix[x][y] = PixelData(Pair(x, y), energy(x, y, originalImage), 0.0, hEnergy)
            }
        }
        for (y in 0 until originalImage.height) {
            for (x in 0 until originalImage.width) {
                val vEnergy = if (y == 0) energyMatrix[x][y].energy else (List(3) {
                    energyMatrix[(x - 1 + it).coerceIn(
                        0,
                        energyMatrix.lastIndex
                    )][y - 1].vEnergy
                }.minOrNull() ?: 0.0) + energyMatrix[x][y].energy
                energyMatrix[x][y].vEnergy = vEnergy
            }
        }
        return energyMatrix
    }

    fun seamFinder(energyMatrix: List<MutableList<PixelData>>, direction: String): MutableList<PixelData> {
        if (direction == "width") {
            val seam =
                mutableListOf(List(energyMatrix.size) { energyMatrix[it][energyMatrix[0].size - 1] }.minByOrNull { it.vEnergy }
                    ?: PixelData())
            for (y in energyMatrix[0].size - 2 downTo 0) {
                seam.add(List(3) {
                    energyMatrix[(seam.last().cord.first - 1 + it).coerceIn(
                        0,
                        energyMatrix.lastIndex
                    )][y]
                }.minByOrNull { it.vEnergy } ?: PixelData())
            }
            return seam
        } else {
            val seam =
                mutableListOf(List(energyMatrix[0].size) { energyMatrix[energyMatrix.size - 1][it] }.minByOrNull { it.hEnergy }
                    ?: PixelData())
            for (x in energyMatrix.size - 2 downTo 0) {
                seam.add(List(3) {
                    energyMatrix[x][(seam.last().cord.second - 1 + it).coerceIn(
                        0,
                        energyMatrix[0].lastIndex
                    )]
                }.minByOrNull { it.hEnergy } ?: PixelData())
            }
            return seam
        }
    }

    fun reduce(direction: String, amount: Int, originalImage: BufferedImage): BufferedImage {
        var image = originalImage
        repeat(amount) {
            val seam = seamFinder(createEnergyMatrix(image), direction)
            if (direction == "width") {
                val newImage = BufferedImage(image.width - 1, image.height, BufferedImage.TYPE_INT_RGB)
                for (pixel in seam) {
                    for (x in 0 until pixel.cord.first) {
                        newImage.setRGB(x, pixel.cord.second, image.getRGB(x, pixel.cord.second))
                    }
                    for (x in pixel.cord.first until newImage.width) {
                        newImage.setRGB(x, pixel.cord.second, image.getRGB(x + 1, pixel.cord.second))
                    }
                }
                image = newImage
            } else {
                val newImage = BufferedImage(image.width, image.height - 1, BufferedImage.TYPE_INT_RGB)
                for (pixel in seam) {
                    for (y in 0 until pixel.cord.second) {
                        newImage.setRGB(pixel.cord.first, y, image.getRGB(pixel.cord.first, y))
                    }
                    for (y in pixel.cord.second until newImage.height) {
                        newImage.setRGB(pixel.cord.first, y, image.getRGB(pixel.cord.first, y + 1))
                    }
                }
                image = newImage
            }
        }
        return image
    }

    fun main(args: Array<String>) {
        val image: BufferedImage = ImageIO.read(File(args[1]))
        val reduceWidth = args[5].toInt()
        val reduceHeight = args[7].toInt()
        val outPut = reduce("height", reduceHeight, reduce("width", reduceWidth, image))
        ImageIO.write(outPut, "png", File(args[3]))
    }
}
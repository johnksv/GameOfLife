package gol.s305089.sound;

/**
 *
 * //TODO JAVADOC
 * 
 * Created: 1.05.2016
 * @author S305089 - John Kasper Svergja
 */
public enum Tone {
    /**
     * All tones are copied from http://www.phy.mtu.edu/~suits/notefreqs.html
     */
    C0(16.35), CSharp0(17.32), D0(18.35), DSharp0(19.45), E0(20.60), F0(21.83),
    FSharp0(23.12), G0(24.50), GSharp0(25.96), A0(27.50), ASharp0(29.14), B0(30.87),
    C1(32.70), CSharp1(34.65), D1(36.71), DSharp1(38.89), E1(41.20), F1(43.65),
    FSharp1(46.25), G1(49.00), GSharp1(51.91), A1(55.00), ASharp1(58.27), B1(61.74),
    C2(65.41), CSharp2(69.30), D2(73.42), DSharp2(77.78), E2(82.41), F2(87.31),
    FSharp2(92.50), G2(98.00), GSharp2(103.83), A2(110.00), ASharp2(116.54), B2(123.47),
    C3(130.81), CSharp3(138.59), D3(146.83), DSharp3(155.56), E3(164.81), F3(174.61),
    FSharp3(185.00), G3(196.00), GSharp3(207.65), A3(220.00), ASharp3(233.08), B3(246.94),
    C4(261.63), CSharp4(277.18), D4(293.66), DSharp4(311.13), E4(329.63), F4(349.23),
    FSharp4(369.99), G4(392.00), GSharp4(415.30), A4(440.00), ASharp4(466.16), B4(493.88),
    C5(523.25), CSharp5(554.37), D5(587.33), DSharp5(622.25), E5(659.25), F5(698.46),
    FSharp5(739.99), G5(783.99), GSharp5(830.61), A5(880.00), ASharp5(932.33), B5(987.77),
    C6(1046.50), CSharp6(1108.73), D6(1174.66), DSharp6(1244.51), E6(1318.51), F6(1396.91),
    FSharp6(1479.98), G6(1567.98), GSharp6(1661.22), A6(1760.00), ASharp6(1864.66), B6(1975.53),
    C7(2093.00), CSharp7(2217.46), D7(2349.32), DSharp7(2489.02), E7(2637.02), F7(2793.83),
    FSharp7(2959.96), G7(3135.96), GSharp7(3322.44), A7(3520.00), ASharp7(3729.31), B7(3951.07),
    C8(4186.01), CSharp8(4434.92), D8(4698.63), DSharp8(4978.03), E8(5274.04), F8(5587.65),
    FSharp8(5919.91), G8(6271.93), GSharp8(6644.88), A8(7040.00), ASharp8(7458.62), B8(7902.13);

    private final double frequency;

    private Tone(double frequency) {
        this.frequency = frequency;
    }

    public double getFreq() {
        return frequency;
    }

    @Override
    public String toString() {
        return this.name() +", frequency: " + frequency;
    }
}

package am.ik.wasm;

/**
 * <a href=
 * "https://webassembly.github.io/spec/core/binary/instructions.html">Instructions</a>
 */
public interface Instruction {

	public static final int UNREACHABLE = 0X00;

	public static final int NOP = 0X01;

	public static final int BLOCK = 0X02;

	public static final int LOOP = 0X03;

	public static final int IF = 0X04;

	public static final int ELSE = 0X05;

	public static final int END = 0X0B;

	public static final int BR = 0X0C;

	public static final int BR_IF = 0X0D;

	public static final int BR_TABLE = 0X0E;

	public static final int RETURN = 0X0F;

	public static final int CALL = 0X10;

	public static final int CALL_INDIRECT = 0X11;

	public static final int DROP = 0X1A;

	public static final int SELECT = 0X1B;

	public static final int GET_LOCAL = 0X20;

	public static final int SET_LOCAL = 0X21;

	public static final int TEE_LOCAL = 0X22;

	public static final int GET_GLOBAL = 0X23;

	public static final int SET_GLOBAL = 0X24;

	public static final int I32_LOAD = 0X28;

	public static final int I64_LOAD = 0X29;

	public static final int F32_LOAD = 0X2A;

	public static final int F64_LOAD = 0X2B;

	public static final int I32_LOAD8_S = 0X2C;

	public static final int I32_LOAD8_U = 0X2D;

	public static final int I32_LOAD16_S = 0X2E;

	public static final int I32_LOAD16_U = 0X2F;

	public static final int I64_LOAD8_S = 0X30;

	public static final int I64_LOAD8_U = 0X31;

	public static final int I64_LOAD16_S = 0X32;

	public static final int I64_LOAD16_U = 0X33;

	public static final int I64_LOAD32_S = 0X34;

	public static final int I64_LOAD32_U = 0X35;

	public static final int I32_STORE = 0X36;

	public static final int I64_STORE = 0X37;

	public static final int F32_STORE = 0X38;

	public static final int F64_STORE = 0X39;

	public static final int I32_STORE8 = 0X3A;

	public static final int I32_STORE16 = 0X3B;

	public static final int I64_STORE8 = 0X3C;

	public static final int I64_STORE16 = 0X3D;

	public static final int I64_STORE32 = 0X3E;

	public static final int CURRENT_MEMORY = 0X3F;

	public static final int GROW_MEMORY = 0X40;

	public static final int I32_CONST = 0X41;

	public static final int I64_CONST = 0X42;

	public static final int F32_CONST = 0X43;

	public static final int F64_CONST = 0X44;

	public static final int I32_EQZ = 0X45;

	public static final int I32_EQ = 0X46;

	public static final int I32_NE = 0X47;

	public static final int I32_LT_S = 0X48;

	public static final int I32_LT_U = 0X49;

	public static final int I32_GT_S = 0X4A;

	public static final int I32_GT_U = 0X4B;

	public static final int I32_LE_S = 0X4C;

	public static final int I32_LE_U = 0X4D;

	public static final int I32_GE_S = 0X4E;

	public static final int I32_GE_U = 0X4F;

	public static final int I64_EQZ = 0X50;

	public static final int I64_EQ = 0X51;

	public static final int I64_NE = 0X52;

	public static final int I64_LT_S = 0X53;

	public static final int I64_LT_U = 0X54;

	public static final int I64_GT_S = 0X55;

	public static final int I64_GT_U = 0X56;

	public static final int I64_LE_S = 0X57;

	public static final int I64_LE_U = 0X58;

	public static final int I64_GE_S = 0X59;

	public static final int I64_GE_U = 0X5A;

	public static final int F32_EQ = 0X5B;

	public static final int F32_NE = 0X5C;

	public static final int F32_LT = 0X5D;

	public static final int F32_GT = 0X5E;

	public static final int F32_LE = 0X5F;

	public static final int F32_GE = 0X60;

	public static final int F64_EQ = 0X61;

	public static final int F64_NE = 0X62;

	public static final int F64_LT = 0X63;

	public static final int F64_GT = 0X64;

	public static final int F64_LE = 0X65;

	public static final int F64_GE = 0X66;

	public static final int I32_CLZ = 0X67;

	public static final int I32_CTZ = 0X68;

	public static final int I32_POPCNT = 0X69;

	public static final int I32_ADD = 0X6A;

	public static final int I32_SUB = 0X6B;

	public static final int I32_MUL = 0X6C;

	public static final int I32_DIV_S = 0X6D;

	public static final int I32_DIV_U = 0X6E;

	public static final int I32_REM_S = 0X6F;

	public static final int I32_REM_U = 0X70;

	public static final int I32_AND = 0X71;

	public static final int I32_OR = 0X72;

	public static final int I32_XOR = 0X73;

	public static final int I32_SHL = 0X74;

	public static final int I32_SHR_S = 0X75;

	public static final int I32_SHR_U = 0X76;

	public static final int I32_ROTL = 0X77;

	public static final int I32_ROTR = 0X78;

	public static final int I64_CLZ = 0X79;

	public static final int I64_CTZ = 0X7A;

	public static final int I64_POPCNT = 0X7B;

	public static final int I64_ADD = 0X7C;

	public static final int I64_SUB = 0X7D;

	public static final int I64_MUL = 0X7E;

	public static final int I64_DIV_S = 0X7F;

	public static final int I64_DIV_U = 0X80;

	public static final int I64_REM_S = 0X81;

	public static final int I64_REM_U = 0X82;

	public static final int I64_AND = 0X83;

	public static final int I64_OR = 0X84;

	public static final int I64_XOR = 0X85;

	public static final int I64_SHL = 0X86;

	public static final int I64_SHR_S = 0X87;

	public static final int I64_SHR_U = 0X88;

	public static final int I64_ROTL = 0X89;

	public static final int I64_ROTR = 0X8A;

	public static final int F32_ABS = 0X8B;

	public static final int F32_NEG = 0X8C;

	public static final int F32_CEIL = 0X8D;

	public static final int F32_FLOOR = 0X8E;

	public static final int F32_TRUNC = 0X8F;

	public static final int F32_NEAREST = 0X90;

	public static final int F32_SQRT = 0X91;

	public static final int F32_ADD = 0X92;

	public static final int F32_SUB = 0X93;

	public static final int F32_MUL = 0X94;

	public static final int F32_DIV = 0X95;

	public static final int F32_MIN = 0X96;

	public static final int F32_MAX = 0X97;

	public static final int F32_COPYSIGN = 0X98;

	public static final int F64_ABS = 0X99;

	public static final int F64_NEG = 0X9A;

	public static final int F64_CEIL = 0X9B;

	public static final int F64_FLOOR = 0X9C;

	public static final int F64_TRUNC = 0X9D;

	public static final int F64_NEAREST = 0X9E;

	public static final int F64_SQRT = 0X9F;

	public static final int F64_ADD = 0XA0;

	public static final int F64_SUB = 0XA1;

	public static final int F64_MUL = 0XA2;

	public static final int F64_DIV = 0XA3;

	public static final int F64_MIN = 0XA4;

	public static final int F64_MAX = 0XA5;

	public static final int F64_COPYSIGN = 0XA6;

	public static final int I32_WRAP_I64 = 0XA7;

	public static final int I32_TRUNC_S_F32 = 0XA8;

	public static final int I32_TRUNC_U_F32 = 0XA9;

	public static final int I32_TRUNC_S_F64 = 0XAA;

	public static final int I32_TRUNC_U_F64 = 0XAB;

	public static final int I64_EXTEND_S_I32 = 0XAC;

	public static final int I64_EXTEND_U_I32 = 0XAD;

	public static final int I64_TRUNC_S_F32 = 0XAE;

	public static final int I64_TRUNC_U_F32 = 0XAF;

	public static final int I64_TRUNC_S_F64 = 0XB0;

	public static final int I64_TRUNC_U_F64 = 0XB1;

	public static final int F32_CONVERT_S_I32 = 0XB2;

	public static final int F32_CONVERT_U_I32 = 0XB3;

	public static final int F32_CONVERT_S_I64 = 0XB4;

	public static final int F32_CONVERT_U_I64 = 0XB5;

	public static final int F32_DEMOTE_F64 = 0XB6;

	public static final int F64_CONVERT_S_I32 = 0XB7;

	public static final int F64_CONVERT_U_I32 = 0XB8;

	public static final int F64_CONVERT_S_I64 = 0XB9;

	public static final int F64_CONVERT_U_I64 = 0XBA;

	public static final int F64_PROMOTE_F32 = 0XBB;

	public static final int I32_REINTERPRET_F32 = 0XBC;

	public static final int I64_REINTERPRET_F64 = 0XBD;

	public static final int F32_REINTERPRET_I32 = 0XBE;

	public static final int F64_REINTERPRET_I64 = 0XBF;

}
